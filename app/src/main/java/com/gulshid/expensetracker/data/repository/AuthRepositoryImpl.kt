package com.gulshid.expensetracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.gulshid.expensetracker.data.Resource
import com.gulshid.expensetracker.domain.model.User
import com.gulshid.expensetracker.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore   // FIX 2: injected Firestore
) : AuthRepository {

    override fun loginWithEmail(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                emit(
                    Resource.Success(
                        User(
                            uid         = firebaseUser.uid,
                            email       = firebaseUser.email ?: "",
                            displayName = firebaseUser.displayName ?: ""
                        )
                    )
                )
            } else {
                emit(Resource.Error(Exception("Authentication failed: user is null")))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            // Step 1 — Create Firebase Auth account
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: throw Exception("Registration failed: user is null")

            // Step 2 — Update display name on the Firebase Auth profile
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Step 3 — FIX 2: Write user document to Firestore
            // Path: /users/{uid}
            val userDocument = mapOf(
                "uid"         to firebaseUser.uid,
                "email"       to (firebaseUser.email ?: ""),
                "displayName" to displayName,
                "createdAt"   to FieldValue.serverTimestamp()
            )
            firestore
                .collection("users")
                .document(firebaseUser.uid)
                .set(userDocument)
                .await()

            emit(
                Resource.Success(
                    User(
                        uid         = firebaseUser.uid,
                        email       = firebaseUser.email ?: "",
                        displayName = displayName
                    )
                )
            )
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return User(
            uid         = firebaseUser.uid,
            email       = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: ""
        )
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}
