package com.foobarust.domain.models

/**
 * Created by kevin on Sep, 2020
 */

data class AuthProfile(
    val id: String,
    val email: String,
    val username: String
)

fun AuthProfile.asUserDetail(): UserDetail {
    return UserDetail(username = username, email = email)
}