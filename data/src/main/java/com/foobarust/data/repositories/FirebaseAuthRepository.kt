package com.foobarust.data.repositories

import android.content.Context
import com.foobarust.domain.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created by kevin on 8/26/20
 */
const val REQUEST_AUTH_EMAIL_CONTINUE_URL = "http://localhost/emailSignInLink"

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : AuthRepository {

    @Throws(Exception::class)
    override suspend fun sendAuthEmail(email: String) {
        val actionCodeSettings = actionCodeSettings {
            // TODO: This link will redirect the user to this URL if the app is not installed on their device and the app was not able to be installed.
            url = REQUEST_AUTH_EMAIL_CONTINUE_URL

            // The sign-in operation has to always be completed in the app unlike other out of band email actions (password reset and email verifications).
            handleCodeInApp = true

            // This will try to open the link in an iOS app if it is installed.
            iosBundleId = "com.example.ios"

            // Sets the Android package name. This will try to open the link in an android app if it is installed.
            // If installIfNotAvailable is set to true, it specifies whether to install the Android app if the device supports it and the app is not already installed.
            // If minimumVersion is specified, and an older version of the app is installed, the user is taken to the Play Store to upgrade the app.
            // The Android app needs to be registered in the Console.
            setAndroidPackageName(
                context.packageName,
                true,       /* installIfNotAvailable */
                null        /* minimumVersion */
            )

            dynamicLinkDomain = "foobarust.page.link"
        }

        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings).await()
    }

    @Throws(Exception::class)
    override suspend fun signInWithEmailLink(email: String, emailLink: String): Boolean {
        // Checks if an incoming link is a sign-in with email link.
        val result = firebaseAuth.signInWithEmailLink(email, emailLink).await()
        return result.user != null
    }

    override suspend fun checkEmailLinkIsValid(emailLink: String): Boolean {
        return firebaseAuth.isSignInWithEmailLink(emailLink)
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun isSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    @Throws(Exception::class)
    override suspend fun reloadUser() {
        firebaseAuth.currentUser?.reload()?.await()
    }
}