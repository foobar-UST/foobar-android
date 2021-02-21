package com.foobarust.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.foobarust.data.common.PreferencesKeys.EMAIL_TO_VERIFY
import com.foobarust.data.mappers.AuthMapper
import com.foobarust.domain.models.auth.AuthProfile
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.usecases.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created by kevin on 8/26/20
 */

class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val preferences: SharedPreferences,
    private val authMapper: AuthMapper
) : AuthRepository {

    override fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null &&
            firebaseAuth.currentUser?.isAnonymous == false
    }

    override fun getUserId(): String {
        val currentUser = firebaseAuth.currentUser!!
        return currentUser.uid
    }

    override suspend fun getUserIdToken(): String {
        val currentUser = firebaseAuth.currentUser!!
        val tokenResult = currentUser.getIdToken(true).await()
        return tokenResult.token ?: throw Exception("Error getting id token.")
    }

    override fun getAuthProfileObservable(): Flow<AuthState<AuthProfile>> = callbackFlow {
        channel.offer(AuthState.Loading)

        val listener = FirebaseAuth.AuthStateListener { auth ->
            auth.currentUser?.let {
                channel.offer(
                    AuthState.Authenticated(authMapper.toAuthProfile(it))
                )
            } ?: channel.offer(AuthState.Unauthenticated)
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun getSavedAuthEmail(): String {
        return preferences.getString(EMAIL_TO_VERIFY, null) ?:
            throw Exception("No saved email.")
    }

    override suspend fun updateSavedAuthEmail(email: String) {
        preferences.edit { putString(EMAIL_TO_VERIFY, email) }
    }

    override suspend fun removeSavedAuthEmail() {
        preferences.edit { remove(EMAIL_TO_VERIFY) }
    }

    override suspend fun requestAuthEmail(email: String) {
        val actionCodeSettings = actionCodeSettings {
            url = "https://foobar-group-delivery-app.web.app/auth"
            handleCodeInApp = true
            iosBundleId = "com.example.ios"
            dynamicLinkDomain = "foobarust2.page.link"
            setAndroidPackageName(
                context.packageName,
                true,       /* installIfNotAvailable */
                null        /* minimumVersion */
            )
        }

        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings).await()
    }

    override suspend fun signInWithEmailLink(email: String, emailLink: String) {
        if (!firebaseAuth.isSignInWithEmailLink(emailLink)) {
            throw Exception("Invalid sign in link.")
        }

        val authResult = firebaseAuth.signInWithEmailLink(email, emailLink).await()

        if (authResult.user == null) {
            throw Exception("Failed to sign in.")
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}