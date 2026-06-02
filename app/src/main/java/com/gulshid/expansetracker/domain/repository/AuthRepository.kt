package com.gulshid.expansetracker.domain.repository


import com.gulshid.expansetracker.domain.model.User
import com.gulshid.expansetracker.data.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: User?

    fun loginWithEmail(email: String, password: String): Flow<Resource<User>>
    fun registerWithEmail(email: String, password: String, name: String): Flow<Resource<User>>
    fun logout()
}