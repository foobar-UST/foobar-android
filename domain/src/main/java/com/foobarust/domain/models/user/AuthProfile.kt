package com.foobarust.domain.models.user

/**
 * Created by kevin on Sep, 2020
 */

data class AuthProfile(
    val id: String,
    val email: String,
    val username: String
)

fun AuthProfile.asUserDetail(): UserDetail {
    return UserDetail(
        id = id,
        username = username,
        email = email
    )
}