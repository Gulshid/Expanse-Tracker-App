package com.gulshid.expensetracker.domain.repository


import com.gulshid.expensetracker.domain.model.User
import com.gulshid.expensetracker.data.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: User?



    fun loginWithEmail(email: String, password: String): Flow<Resource<User>>
    fun registerWithEmail(email: String, password: String, name: String): Flow<Resource<User>>
    fun logout()
}