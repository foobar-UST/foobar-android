package com.foobarust.data.mappers

import com.foobarust.domain.models.AuthProfile
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * Created by kevin on 9/21/20
 */

class AuthMapper @Inject constructor() {

    fun toAuthProfile(firebaseUser: FirebaseUser): AuthProfile {
        val email = firebaseUser.email!!

        @Suppress("SimpleRedundantLet")
        return AuthProfile(
            username = getUserNameFromEmail(email),
            email = email,
            photoUrl = firebaseUser.photoUrl?.let { it.toString() }
        )
    }

    private fun getUserNameFromEmail(email: String): String {
        return email.substring(0, email.indexOf('@'))
    }
}