package com.gulshid.expensetracker.domain.usecase

import com.gulshid.expensetracker.data.Resource
import com.gulshid.expensetracker.domain.model.Expense
import com.gulshid.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    operator fun invoke(userId: String): Flow<Resource<List<Expense>>> {
        return expenseRepository.getExpenses(userId)
    }
}
