package com.gulshid.expensetracker.domain.usecase

import com.gulshid.expensetracker.data.Resource
import com.gulshid.expensetracker.domain.repository.ExpenseRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(userId: String, expenseId: String): Resource<Unit> {
        return expenseRepository.deleteExpense(userId, expenseId)
    }
}
