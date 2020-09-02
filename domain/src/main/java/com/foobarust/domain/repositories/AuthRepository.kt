package com.foobarust.domain.repositories

/**
 * Created by kevin on 8/26/20
 */

interface AuthRepository {

    suspend fun sendAuthEmail(email: String)

    suspend fun signInWithEmailLink(email: String, emailLink: String): Boolean

    suspend fun checkEmailLinkIsValid(emailLink: String): Boolean

    suspend fun signOut()

    suspend fun isSignedIn(): Boolean

    suspend fun reloadUser()
}