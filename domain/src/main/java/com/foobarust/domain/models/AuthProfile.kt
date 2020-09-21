package com.foobarust.domain.models

/**
 * Created by kevin on Sep, 2020
 */

data class AuthProfile(
    val username: String,
    val email: String,
    val photoUrl: String?
)