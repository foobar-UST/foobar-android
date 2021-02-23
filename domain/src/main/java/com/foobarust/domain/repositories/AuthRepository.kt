package com.foobarust.domain.repositories

import com.foobarust.domain.models.auth.AuthProfile
import com.foobarust.domain.usecases.AuthState
import kotlinx.coroutines.flow.SharedFlow

interface AuthRepository {

    val authProfileObservable: SharedFlow<AuthState<AuthProfile>>

    /* User */
    fun isUserSignedIn(): Boolean

    fun getUserId(): String

    suspend fun getUserIdToken(): String

    /* Saved auth email */
    suspend fun getSavedAuthEmail(): String

    suspend fun updateSavedAuthEmail(email: String)

    suspend fun removeSavedAuthEmail()

    /* Sign in */
    suspend fun requestAuthEmail(email: String)

    suspend fun signInWithEmailLink(email: String, emailLink: String)

    fun signOut()
}