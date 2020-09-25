package com.foobarust.data.mappers

import com.foobarust.data.utils.toStringOrNull
import com.foobarust.domain.models.AuthProfile
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * Created by kevin on 9/21/20
 */

class AuthMapper @Inject constructor() {

    fun toAuthProfile(firebaseUser: FirebaseUser): AuthProfile {
        val email = firebaseUser.email!!

        return AuthProfile(
            email = email,
            username = email.substring(0, email.indexOf('@')),
            photoUrl = firebaseUser.photoUrl?.toStringOrNull()
        )
    }
}