package com.gulshid.expensetracker.domain.repository

import com.gulshid.expensetracker.data.Resource
import com.gulshid.expensetracker.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginWithEmail(email: String, password: String): Flow<Resource<User>>
    fun registerWithEmail(email: String, password: String, displayName: String): Flow<Resource<User>>
    fun getCurrentUser(): User?
    fun logout()
}
