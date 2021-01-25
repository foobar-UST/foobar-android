package com.foobarust.domain.repositories

import com.foobarust.domain.models.user.AuthProfile
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun getAuthRequestedEmail(): String

    suspend fun updateAuthRequestedEmail(email: String)

    suspend fun removeAuthRequestedEmail()

    suspend fun isSignedIn(): Boolean

    suspend fun getUserId(): String

    suspend fun getIdToken(): String

    /**
     * Return a observable of auth profile, null if the user is not signed in.
     */
    fun getAuthProfileObservable(): Flow<Resource<AuthProfile?>>

    suspend fun sendEmailWithSignInLink(email: String)

    suspend fun signInWithEmailLink(email: String, emailLink: String)

    suspend fun reloadUser()

    suspend fun signOut()
}