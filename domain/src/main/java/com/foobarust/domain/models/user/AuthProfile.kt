package com.foobarust.domain.models.user

/**
 * Created by kevin on Sep, 2020
 */

data class AuthProfile(
    val id: String? = null,
    val email: String? = null,
    val username: String? = null
)

fun AuthProfile.isSignedIn(): Boolean {
    return id != null
}

fun AuthProfile.asUserDetail(): UserDetail {
    return UserDetail(
        id = id,
        username = username,
        email = email
    )
}