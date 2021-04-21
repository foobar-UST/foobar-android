package com.foobarust.domain.repositories

import com.foobarust.domain.models.auth.AuthProfile
import com.foobarust.domain.usecases.AuthState
import kotlinx.coroutines.flow.SharedFlow

interface AuthRepository {

    val authProfileObservable: SharedFlow<AuthState<AuthProfile>>

    fun isUserSignedIn(): Boolean

    fun getUserId(): String

    suspend fun getUserIdToken(): String

    suspend fun getSavedAuthEmail(): String

    suspend fun updateSavedAuthEmail(email: String)

    suspend fun removeSavedAuthEmail()

    suspend fun requestAuthEmail(email: String)

    suspend fun signInWithEmailLink(email: String, emailLink: String)

    fun signOut()
}