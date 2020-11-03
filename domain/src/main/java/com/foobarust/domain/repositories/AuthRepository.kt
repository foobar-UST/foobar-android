package com.foobarust.domain.repositories

import com.foobarust.domain.models.AuthProfile
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun getAuthRequestedEmail(): Flow<Resource<String?>>

    suspend fun updateAuthRequestedEmail(email: String?)

    suspend fun isSignedIn(): Boolean

    suspend fun getAuthUserId(): String

    fun getAuthProfileObservable(): Flow<Resource<AuthProfile>>

    suspend fun sendEmailWithSignInLink(email: String)

    suspend fun signInWithEmailLink(email: String, emailLink: String)

    suspend fun reloadUser()

    suspend fun signOut()
}