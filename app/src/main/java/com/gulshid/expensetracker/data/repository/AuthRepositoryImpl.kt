package com.gulshid.expensetracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.gulshid.expensetracker.data.Resource
import com.gulshid.expensetracker.domain.model.User
import com.gulshid.expensetracker.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: User?
        get() = firebaseAuth.currentUser?.let {
            User(uid = it.uid, email = it.email ?: "")
        }

    override fun loginWithEmail(email: String, password: String): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading)
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val user = User(uid = firebaseUser.uid, email = firebaseUser.email ?: "")
                    trySend(Resource.Success(user))
                } else {
                    trySend(Resource.Error(Exception("User is null")))
                }
            }
            .addOnFailureListener { exception ->
                trySend(Resource.Error(exception))
            }
        awaitClose()
    }

    override fun registerWithEmail(email: String, password: String, name: String): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val user = User(uid = firebaseUser.uid, email = firebaseUser.email ?: "", displayName = name)
                    trySend(Resource.Success(user))
                } else {
                    trySend(Resource.Error(Exception("Registration failed")))
                }
            }
            .addOnFailureListener { exception ->
                trySend(Resource.Error(exception))
            }
        awaitClose()
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}