package com.gulshid.expensetracker.domain.usecase

import com.gulshid.expensetracker.data.Resource
import com.gulshid.expensetracker.domain.model.Expense
import com.gulshid.expensetracker.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense): Resource<Unit> =
        expenseRepository.addExpense(expense)
}
