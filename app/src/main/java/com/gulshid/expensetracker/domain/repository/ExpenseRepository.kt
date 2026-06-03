package com.gulshid.expensetracker.domain.repository

import com.gulshid.expensetracker.data.Resource
import com.gulshid.expensetracker.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpenses(userId: String): Flow<Resource<List<Expense>>>
    suspend fun addExpense(expense: Expense): Resource<Unit>
    suspend fun deleteExpense(userId: String, expenseId: String): Resource<Unit>
    suspend fun updateExpense(expense: Expense): Resource<Unit>
}
