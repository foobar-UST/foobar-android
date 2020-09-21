package com.foobarust.domain.repositories

import com.foobarust.domain.models.AuthProfile
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun isSignedIn(): Boolean

    suspend fun getAuthUid(): String

    fun getAuthProfileObservable(): Flow<Resource<AuthProfile>>

    suspend fun sendEmailWithSignInLink(email: String)

    suspend fun signInWithEmailLink(email: String, emailLink: String)

    suspend fun reloadAuthInfo()

    suspend fun signOut()
}