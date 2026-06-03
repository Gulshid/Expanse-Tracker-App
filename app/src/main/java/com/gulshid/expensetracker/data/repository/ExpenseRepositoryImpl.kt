package com.gulshid.expensetracker.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.gulshid.expensetracker.data.Resource
import com.gulshid.expensetracker.data.model.ExpenseDto
import com.gulshid.expensetracker.domain.model.Expense
import com.gulshid.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ExpenseRepository {

    // Helper: returns the /users/{userId}/expenses collection reference
    private fun expensesCollection(userId: String) =
        firestore.collection("users").document(userId).collection("expenses")

    /**
     * Real-time stream using callbackFlow + Firestore snapshot listener.
     * Emits Resource.Loading once, then Resource.Success on every snapshot update.
     * Offline persistence (enabled in ExpenseTrackerApp) means this still
     * emits cached data when the device is offline.
     */
    override fun getExpenses(userId: String): Flow<Resource<List<Expense>>> = callbackFlow {
        trySend(Resource.Loading)

        val listenerRegistration = expensesCollection(userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error))
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ExpenseDto::class.java)?.toDomain()
                } ?: emptyList()

                trySend(Resource.Success(expenses))
            }

        // Cancel the Firestore listener when the Flow collector is cancelled
        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Adds a new expense document.
     * Firestore auto-generates the document ID.
     * Path: /users/{userId}/expenses/{autoId}
     */
    override suspend fun addExpense(expense: Expense): Resource<Unit> {
        return try {
            expensesCollection(expense.userId)
                .add(ExpenseDto.fromDomain(expense))
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    /**
     * Deletes a specific expense document by its Firestore ID.
     */
    override suspend fun deleteExpense(userId: String, expenseId: String): Resource<Unit> {
        return try {
            expensesCollection(userId)
                .document(expenseId)
                .delete()
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    /**
     * Updates an existing expense document (full overwrite of mutable fields).
     */
    override suspend fun updateExpense(expense: Expense): Resource<Unit> {
        return try {
            expensesCollection(expense.userId)
                .document(expense.id)
                .update(ExpenseDto.fromDomain(expense))
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
