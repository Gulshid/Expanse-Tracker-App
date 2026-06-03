package com.gulshid.expensetracker.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gulshid.expensetracker.data.Resource
import com.gulshid.expensetracker.domain.model.Expense
import com.gulshid.expensetracker.domain.usecase.AddExpenseUseCase
import com.gulshid.expensetracker.domain.usecase.DeleteExpenseUseCase
import com.gulshid.expensetracker.domain.usecase.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

enum class ExpenseFilter { ALL, THIS_WEEK, THIS_MONTH, LAST_3_MONTHS }

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    val currentUserId: String get() = firebaseAuth.currentUser?.uid ?: ""

    // Raw live list from Firestore
    private val _expenseListState = MutableStateFlow<ExpenseListState>(ExpenseListState.Loading)
    val expenseListState: StateFlow<ExpenseListState> = _expenseListState.asStateFlow()

    // Currently applied filter — History observes this
    private val _activeFilter = MutableStateFlow(ExpenseFilter.ALL)
    val activeFilter: StateFlow<ExpenseFilter> = _activeFilter.asStateFlow()

    // Active category filter — Dashboard category chips
    private val _activeCategoryFilter = MutableStateFlow("All")
    val activeCategoryFilter: StateFlow<String> = _activeCategoryFilter.asStateFlow()

    // Add / delete result
    private val _actionState = MutableStateFlow<ExpenseState>(ExpenseState.Idle)
    val actionState: StateFlow<ExpenseState> = _actionState.asStateFlow()

    init {
        if (currentUserId.isNotBlank()) loadExpenses()
    }

    fun loadExpenses() {
        viewModelScope.launch {
            getExpensesUseCase(currentUserId).collectLatest { result ->
                _expenseListState.value = when (result) {
                    is Resource.Loading -> ExpenseListState.Loading
                    is Resource.Success -> ExpenseListState.Success(result.data)
                    is Resource.Error   -> ExpenseListState.Error(
                        result.exception.localizedMessage ?: "Failed to load"
                    )
                }
            }
        }
    }

    fun setFilter(filter: ExpenseFilter) { _activeFilter.value = filter }
    fun setCategoryFilter(category: String) { _activeCategoryFilter.value = category }

    /** Returns expenses sliced by the active time filter */
    fun filteredExpenses(all: List<Expense>): List<Expense> {
        val cutoff = when (_activeFilter.value) {
            ExpenseFilter.ALL          -> 0L
            ExpenseFilter.THIS_WEEK    -> daysAgo(7)
            ExpenseFilter.THIS_MONTH   -> daysAgo(30)
            ExpenseFilter.LAST_3_MONTHS -> daysAgo(90)
        }
        return if (cutoff == 0L) all else all.filter { it.timestamp >= cutoff }
    }

    /** Returns expenses sliced by category for the Dashboard chips */
    fun categoryFilteredExpenses(all: List<Expense>): List<Expense> {
        val cat = _activeCategoryFilter.value
        return if (cat == "All") all else all.filter { it.category == cat }
    }

    /** Category totals map for the pie chart */
    fun categoryTotals(all: List<Expense>): Map<String, Double> =
        all.groupBy { it.category }.mapValues { (_, items) -> items.sumOf { it.amount } }

    fun saveExpense(
        amount: Double,
        category: String,
        description: String,
        paymentMethod: String,
        timestampMillis: Long
    ) {
        if (currentUserId.isBlank()) { _actionState.value = ExpenseState.Error("Not logged in"); return }
        val expense = Expense(
            userId        = currentUserId,
            amount        = amount,
            category      = category,
            description   = description,
            paymentMethod = paymentMethod,
            timestamp     = timestampMillis
        )
        viewModelScope.launch {
            _actionState.value = ExpenseState.Loading
            _actionState.value = when (val r = addExpenseUseCase(expense)) {
                is Resource.Success -> ExpenseState.SaveSuccess
                is Resource.Error   -> ExpenseState.Error(r.exception.localizedMessage ?: "Failed to save")
                else -> ExpenseState.Idle
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            _actionState.value = ExpenseState.Loading
            _actionState.value = when (val r = deleteExpenseUseCase(currentUserId, expenseId)) {
                is Resource.Success -> ExpenseState.DeleteSuccess
                is Resource.Error   -> ExpenseState.Error(r.exception.localizedMessage ?: "Failed to delete")
                else -> ExpenseState.Idle
            }
        }
    }

    fun resetActionState() { _actionState.value = ExpenseState.Idle }

    private fun daysAgo(days: Int): Long = Calendar.getInstance()
        .apply { add(Calendar.DAY_OF_YEAR, -days) }.timeInMillis
}
