package com.foobarust.data.mappers

import com.foobarust.data.models.UserDetailEntity
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
            id = firebaseUser.uid,
            email = email,
            username = getUsernameFromEmail(email)
        )
    }

    fun toUserDetailEntity(firebaseUser: FirebaseUser): UserDetailEntity {
        val email = firebaseUser.email!!

        return UserDetailEntity(
            email = email,
            username = getUsernameFromEmail(email)
        )
    }

    private fun getUsernameFromEmail(email: String): String {
        return email.substring(0, email.indexOf('@'))
    }
}