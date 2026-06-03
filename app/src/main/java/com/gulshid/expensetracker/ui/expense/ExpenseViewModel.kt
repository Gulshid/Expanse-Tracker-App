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
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    // Current user's uid — never null after login
    val currentUserId: String
        get() = firebaseAuth.currentUser?.uid ?: ""

    // ── Expense list state (real-time stream) ──────────────────────
    private val _expenseListState = MutableStateFlow<ExpenseListState>(ExpenseListState.Loading)
    val expenseListState: StateFlow<ExpenseListState> = _expenseListState.asStateFlow()

    // ── Action state (add / delete result) ──────────────────────────
    private val _actionState = MutableStateFlow<ExpenseState>(ExpenseState.Idle)
    val actionState: StateFlow<ExpenseState> = _actionState.asStateFlow()

    init {
        // Start listening as soon as ViewModel is created (if user is logged in)
        if (currentUserId.isNotBlank()) loadExpenses()
    }

    // ── Load real-time expenses from Firestore ───────────────────────
    fun loadExpenses() {
        viewModelScope.launch {
            getExpensesUseCase(currentUserId).collectLatest { result ->
                _expenseListState.value = when (result) {
                    is Resource.Loading -> ExpenseListState.Loading
                    is Resource.Success -> ExpenseListState.Success(result.data)
                    is Resource.Error   -> ExpenseListState.Error(
                        result.exception.localizedMessage ?: "Failed to load expenses"
                    )
                }
            }
        }
    }

    // ── Save a new expense ───────────────────────────────────────────
    fun saveExpense(
        amount: Double,
        category: String,
        description: String,
        paymentMethod: String,
        timestampMillis: Long
    ) {
        if (currentUserId.isBlank()) {
            _actionState.value = ExpenseState.Error("User not logged in")
            return
        }

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
            _actionState.value = when (val result = addExpenseUseCase(expense)) {
                is Resource.Success -> ExpenseState.SaveSuccess
                is Resource.Error   -> ExpenseState.Error(
                    result.exception.localizedMessage ?: "Failed to save expense"
                )
                else -> ExpenseState.Idle
            }
        }
    }

    // ── Delete an expense ────────────────────────────────────────────
    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            _actionState.value = ExpenseState.Loading
            _actionState.value = when (val result = deleteExpenseUseCase(currentUserId, expenseId)) {
                is Resource.Success -> ExpenseState.DeleteSuccess
                is Resource.Error   -> ExpenseState.Error(
                    result.exception.localizedMessage ?: "Failed to delete expense"
                )
                else -> ExpenseState.Idle
            }
        }
    }

    // ── Reset action state after handling ────────────────────────────
    fun resetActionState() {
        _actionState.value = ExpenseState.Idle
    }
}
