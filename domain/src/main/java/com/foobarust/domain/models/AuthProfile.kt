package com.foobarust.domain.models

/**
 * Created by kevin on Sep, 2020
 */

data class AuthProfile(
    val email: String,
    val username: String,
    val photoUrl: String?
)

/**
 * Get [UserDetail] from local [AuthProfile].
 * Used when the user document is still not yet inserted into the database or there is no
 * connection.
 */
fun AuthProfile.asUserDetail(): UserDetail {
    return UserDetail(
        username = username,
        email = email,
        photoUrl = photoUrl
    )
}