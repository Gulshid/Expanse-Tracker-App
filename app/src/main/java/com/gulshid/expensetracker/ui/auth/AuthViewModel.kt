package com.gulshid.expensetracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gulshid.expensetracker.domain.repository.AuthRepository
import com.gulshid.expensetracker.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password fields cannot be empty")
            return
        }

        viewModelScope.launch {
            authRepository.loginWithEmail(email, password).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.value = AuthState.Loading
                    }
                    is Resource.Success -> {
                        if (result.data != null) {
                            _authState.value = AuthState.Success(result.data)
                        } else {
                            _authState.value = AuthState.Error("User data is missing")
                        }
                    }
                    is Resource.Error -> {
                        // Handled by accessing the standard exception property inside your Resource.Error class
                        val errorMessage = result.exception?.localizedMessage ?: "Authentication Failed"
                        _authState.value = AuthState.Error(errorMessage)
                    }
                }
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}