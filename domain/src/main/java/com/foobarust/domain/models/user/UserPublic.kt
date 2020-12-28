package com.foobarust.domain.models.user

/**
 * Created by kevin on 12/27/20
 */

data class UserPublic(
    val id: String,
    val username: String,
    val photoUrl: String? = null
)