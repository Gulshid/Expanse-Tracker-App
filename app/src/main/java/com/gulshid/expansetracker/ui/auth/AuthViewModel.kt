package com.gulshid.expansetracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gulshid.expansetracker.data.Resource
import com.gulshid.expansetracker.domain.model.User
import com.gulshid.expansetracker.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<User>?>(null)
    val authState: StateFlow<Resource<User>?> = _authState

    val isUserLoggedIn: Boolean
        get() = authRepository.currentUser != null

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.loginWithEmail(email, password).collect { resource ->
                _authState.value = resource
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            authRepository.registerWithEmail(email, password, name).collect { resource ->
                _authState.value = resource
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = null
    }
}