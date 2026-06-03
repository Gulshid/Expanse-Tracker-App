package com.gulshid.expensetracker.domain.repository

import com.gulshid.expensetracker.data.Resource
import com.gulshid.expensetracker.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    // Real-time stream of all expenses for the logged-in user
    fun getExpenses(userId: String): Flow<Resource<List<Expense>>>

    // Add a new expense document under /users/{userId}/expenses/{id}
    suspend fun addExpense(expense: Expense): Resource<Unit>

    // Delete an expense by its Firestore document id
    suspend fun deleteExpense(userId: String, expenseId: String): Resource<Unit>

    // Update an existing expense document
    suspend fun updateExpense(expense: Expense): Resource<Unit>
}
