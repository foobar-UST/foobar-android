package com.foobarust.data.mappers

import com.foobarust.data.common.Constants.USER_EMAIL_FIELD
import com.foobarust.data.common.Constants.USER_USERNAME_FIELD
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

    // TODO: fix toUserDetailEntityMap
    fun toUserDetailEntityMap(firebaseUser: FirebaseUser): MutableMap<String, Any> {
        val email = firebaseUser.email!!

        return mutableMapOf(
            USER_EMAIL_FIELD to email,
            USER_USERNAME_FIELD to getUsernameFromEmail(email)
        )
    }

    private fun getUsernameFromEmail(email: String): String {
        return email.substring(0, email.indexOf('@'))
    }
}