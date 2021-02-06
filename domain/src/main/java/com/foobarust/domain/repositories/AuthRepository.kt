package com.foobarust.domain.repositories

import com.foobarust.domain.models.user.AuthProfile
import com.foobarust.domain.usecases.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    /*
        User
     */
    suspend fun isUserSignedIn(): Boolean

    suspend fun getUserId(): String

    suspend fun getUserIdToken(): String

    // Return a observable of auth profile, null if the user is not signed in.
    fun getAuthProfileObservable(): Flow<AuthState<AuthProfile>>

    /*
        Saved auth email
     */
    suspend fun getSavedAuthEmail(): String

    suspend fun updateSavedAuthEmail(email: String)

    suspend fun removeSavedAuthEmail()

    /*
        Sign in
     */
    suspend fun requestAuthEmail(email: String)

    suspend fun signInWithEmailLink(email: String, emailLink: String)

    suspend fun signOut()
}