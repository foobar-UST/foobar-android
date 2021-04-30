package com.foobarust.testshared.repositories

import com.foobarust.domain.models.auth.AuthProfile
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.usecases.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

/**
 * Created by kevin on 4/9/21
 */

class FakeAuthRepositoryImpl(
    private val idToken: String,
    private val defaultAuthProfile: AuthProfile,
    isSignedIn: Boolean,
    coroutineScope: CoroutineScope
) : AuthRepository {

    private var shouldReturnIOError = false
    private var shouldReturnNetworkError = false

    private var savedAuthEmail: String? = null

    private val _signedInFlow = MutableStateFlow(isSignedIn)

    override val authProfileObservable: SharedFlow<AuthState<AuthProfile>> = _signedInFlow
        .map { signedIn ->
            if (signedIn) {
                AuthState.Authenticated(defaultAuthProfile)
            } else {
                AuthState.Unauthenticated
            }
        }
        .shareIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed()
        )

    override fun isUserSignedIn(): Boolean = _signedInFlow.value

    override fun getUserId(): String = defaultAuthProfile.id

    override suspend fun getUserIdToken(): String {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return idToken
    }

    override suspend fun getSavedAuthEmail(): String {
        if (shouldReturnIOError) throw Exception("IO error.")
        return savedAuthEmail ?: throw Exception("Auth email not found.")
    }

    override suspend fun updateSavedAuthEmail(email: String) {
        if (shouldReturnIOError) throw Exception("IO error.")
        savedAuthEmail = email
    }

    override suspend fun removeSavedAuthEmail() {
        if (shouldReturnIOError) throw Exception("IO error.")
        savedAuthEmail = null
    }

    override suspend fun requestAuthEmail(email: String) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
    }

    override suspend fun signInWithEmailLink(email: String, emailLink: String) {
        if (savedAuthEmail != email) throw Exception("Unmatched auth email.")
        if (shouldReturnNetworkError) throw Exception("Network error.")
        _signedInFlow.value = true
    }

    override fun signOut() {
        if (!_signedInFlow.value) throw Exception("Not signed in.")
        _signedInFlow.value = false
    }

    fun setUserSignedIn(signedIn: Boolean) {
        _signedInFlow.value = signedIn
    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    fun setIOError(value: Boolean) {
        shouldReturnIOError = value
    }
}