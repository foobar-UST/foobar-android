package com.foobarust.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.mappers.AuthMapper
import com.foobarust.data.preferences.PreferencesKeys.PREF_KEY_EMAIL_VERIFY
import com.foobarust.data.utils.getStringFlow
import com.foobarust.domain.models.AuthProfile
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.states.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created by kevin on 8/26/20
 */

private const val ERROR_SIGN_IN_LINK_INVALID = "Invalid sign in link."
private const val ERROR_GET_CURRENT_USER = "Error getting current user."
private const val ERROR_NOT_SIGNED_IN = "User is not signed in."

// TODO: This link will redirect the user to this URL if the app is not installed on their device and the app was not able to be installed.
private const val CONTINUE_URL = "https://foobar-group-delivery-app.web.app/"
private const val DYNAMIC_LINK_DOMAIN = "foobarust.page.link"
private const val IOS_BUNDLE_ID = "com.foobarust.ios"

class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authMapper: AuthMapper,
    private val preferences: SharedPreferences
) : AuthRepository {

    override fun getAuthRequestedEmail(): Flow<Resource<String?>> {
        return preferences.getStringFlow(PREF_KEY_EMAIL_VERIFY)
    }

    override suspend fun updateAuthRequestedEmail(email: String?) {
        preferences.edit { putString(PREF_KEY_EMAIL_VERIFY, email) }
    }

    override suspend fun isSignedIn(): Boolean {
        return firebaseAuth.currentUser != null &&
               firebaseAuth.currentUser?.isAnonymous == false
    }

    override suspend fun getAuthUserId(): String {
        return firebaseAuth.currentUser?.uid!!
    }

    override fun getAuthProfileObservable(): Flow<Resource<AuthProfile>> = channelFlow {
        // Attach auth state listener
        val listener = FirebaseAuth.AuthStateListener {
            val currentUser = it.currentUser

            if (currentUser == null) {
                channel.offer(Resource.Error(ERROR_NOT_SIGNED_IN))
            } else {
                channel.offer(Resource.Success(authMapper.toAuthProfile(currentUser)))
            }
        }.also {
            firebaseAuth.addAuthStateListener(it)
        }

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    @Throws(Exception::class)
    override suspend fun sendEmailWithSignInLink(email: String) {
        val actionCodeSettings = actionCodeSettings {
            url = CONTINUE_URL

            // The sign-in operation has to always be completed in the app unlike other out of band email actions (password reset and email verifications).
            handleCodeInApp = true

            // This will try to open the link in an iOS app if it is installed.
            iosBundleId = IOS_BUNDLE_ID

            // Sets the Android package name. This will try to open the link in an android app if it is installed.
            // If installIfNotAvailable is set to true, it specifies whether to install the Android app if the device supports it and the app is not already installed.
            // If minimumVersion is specified, and an older version of the app is installed, the user is taken to the Play Store to upgrade the app.
            // The Android app needs to be registered in the Console.
            setAndroidPackageName(
                context.packageName,
                true,       /* installIfNotAvailable */
                null        /* minimumVersion */
            )

            dynamicLinkDomain = DYNAMIC_LINK_DOMAIN
        }

        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings).await()
    }

    override suspend fun signInWithEmailLink(email: String, emailLink: String) {
        if (!firebaseAuth.isSignInWithEmailLink(emailLink)) {
            throw Exception(ERROR_SIGN_IN_LINK_INVALID)
        }

        val currentUser = firebaseAuth.signInWithEmailLink(email, emailLink).await().user ?:
            throw Exception(ERROR_GET_CURRENT_USER)

        // Insert a new user document into firestore
        val userDetail = authMapper.toUserDetailEntity(currentUser)

        firestore.collection(USERS_COLLECTION).document(currentUser.uid)
            .set(userDetail, SetOptions.merge())
            .await()
    }

    override suspend fun reloadUser() {
        firebaseAuth.currentUser?.reload()?.await()
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}