package com.foobarust.domain.repository

import com.foobarust.domain.models.auth.AuthProfile
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.usecases.AuthState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * Created by kevin on 4/9/21
 */

class FakeAuthRepositoryImpl : AuthRepository {

    private var userId: String = UUID.randomUUID().toString()
    private var idToken: String = UUID.randomUUID().toString()
    private var signedIn: Boolean = false
    private var savedAuthEmail: String = ""

    private var shouldReturnIOError = false
    private var shouldReturnNetworkError = false

    private var _signedIn = MutableStateFlow(false)

    override val authProfileObservable: SharedFlow<AuthState<AuthProfile>> = _signedIn
        .map { signedIn ->
            if (signedIn) {
                AuthState.Authenticated(
                    AuthProfile(
                        id = userId,
                        email = savedAuthEmail,
                        username = "username"
                    )
                )
            } else AuthState.Unauthenticated
        }
        .shareIn(
            scope = GlobalScope,
            started = SharingStarted.WhileSubscribed()
        )

    override fun isUserSignedIn(): Boolean = signedIn

    override fun getUserId(): String = userId

    override suspend fun getUserIdToken(): String {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return idToken
    }

    override suspend fun getSavedAuthEmail(): String {
        if (shouldReturnIOError) throw Exception("IO error.")
        return savedAuthEmail
    }

    override suspend fun updateSavedAuthEmail(email: String) {
        if (shouldReturnIOError) throw Exception("IO error.")
        savedAuthEmail = email
    }

    override suspend fun removeSavedAuthEmail() {
        if (shouldReturnIOError) throw Exception("IO error.")
        savedAuthEmail = ""
    }

    override suspend fun requestAuthEmail(email: String) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
    }

    override suspend fun signInWithEmailLink(email: String, emailLink: String) {
        if (savedAuthEmail != email) throw Exception("Unmatched email")
        if (shouldReturnNetworkError) throw Exception("Network error.")
        signedIn = true
    }

    override fun signOut() {
        signedIn = false
    }

    fun setUserId(userId: String) {
        this.userId = userId
    }

    fun setUserIdToken(idToken: String) {
        this.idToken = idToken
    }

    fun setUserSignedIn(signedIn: Boolean) {
        this.signedIn = signedIn
    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }
}