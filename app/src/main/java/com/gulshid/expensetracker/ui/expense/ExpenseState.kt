package com.gulshid.expensetracker.ui.expense

import com.gulshid.expensetracker.domain.model.Expense

sealed interface ExpenseState {
    data object Idle : ExpenseState
    data object Loading : ExpenseState
    data object SaveSuccess : ExpenseState
    data object DeleteSuccess : ExpenseState
    data class Error(val message: String) : ExpenseState
}

sealed interface ExpenseListState {
    data object Loading : ExpenseListState
    data class Success(val expenses: List<Expense>) : ExpenseListState
    data class Error(val message: String) : ExpenseListState
}
