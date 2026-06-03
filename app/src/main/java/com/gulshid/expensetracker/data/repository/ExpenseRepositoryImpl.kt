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
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ExpenseRepository {

    private fun expensesCollection(userId: String) =
        firestore.collection("users").document(userId).collection("expenses")

    override fun getExpenses(userId: String): Flow<Resource<List<Expense>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = expensesCollection(userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(Resource.Error(error)); return@addSnapshotListener }
                val list = snapshot?.documents
                    ?.mapNotNull { it.toObject(ExpenseDto::class.java)?.toDomain() }
                    ?: emptyList()
                trySend(Resource.Success(list))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addExpense(expense: Expense): Resource<Unit> = try {
        expensesCollection(expense.userId).add(ExpenseDto.fromDomain(expense)).await()
        Resource.Success(Unit)
    } catch (e: Exception) { Resource.Error(e) }

    override suspend fun deleteExpense(userId: String, expenseId: String): Resource<Unit> = try {
        expensesCollection(userId).document(expenseId).delete().await()
        Resource.Success(Unit)
    } catch (e: Exception) { Resource.Error(e) }

    override suspend fun updateExpense(expense: Expense): Resource<Unit> = try {
        expensesCollection(expense.userId).document(expense.id)
            .update(ExpenseDto.fromDomain(expense)).await()
        Resource.Success(Unit)
    } catch (e: Exception) { Resource.Error(e) }
}
