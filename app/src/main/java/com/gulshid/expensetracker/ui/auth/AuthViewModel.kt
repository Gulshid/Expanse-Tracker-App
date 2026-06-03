package com.gulshid.expensetracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gulshid.expensetracker.data.Resource
import com.gulshid.expensetracker.domain.repository.AuthRepository
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
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        viewModelScope.launch {
            authRepository.loginWithEmail(email, password).collectLatest { result ->
                _authState.value = when (result) {
                    is Resource.Loading -> AuthState.Loading
                    is Resource.Success -> {
                        if (result.data != null) AuthState.Success(result.data)
                        else AuthState.Error("User data is missing")
                    }
                    is Resource.Error -> AuthState.Error(
                        result.exception?.localizedMessage ?: "Authentication failed"
                    )
                }
            }
        }
    }

    fun registerUser(displayName: String, email: String, password: String, confirmPassword: String) {
        when {
            displayName.isBlank() -> {
                _authState.value = AuthState.Error("Name cannot be empty")
                return
            }
            email.isBlank() -> {
                _authState.value = AuthState.Error("Email cannot be empty")
                return
            }
            password.isBlank() -> {
                _authState.value = AuthState.Error("Password cannot be empty")
                return
            }
            password != confirmPassword -> {
                _authState.value = AuthState.Error("Passwords do not match")
                return
            }
            password.length < 6 -> {
                _authState.value = AuthState.Error("Password must be at least 6 characters")
                return
            }
        }
        viewModelScope.launch {
            authRepository.registerWithEmail(email, password, displayName).collectLatest { result ->
                _authState.value = when (result) {
                    is Resource.Loading -> AuthState.Loading
                    is Resource.Success -> {
                        if (result.data != null) AuthState.Success(result.data)
                        else AuthState.Error("Registration failed")
                    }
                    is Resource.Error -> AuthState.Error(
                        result.exception?.localizedMessage ?: "Registration failed"
                    )
                }
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
