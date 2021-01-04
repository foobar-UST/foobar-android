package com.foobarust.domain.models.user

import java.util.*

/**
 * Created by kevin on 9/12/20
 */

data class UserDetail(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val name: String? = null,
    val phoneNum: String? = null,
    val photoUrl: String? = null,
    val updatedAt: Date? = null
)

fun UserDetail.isSignedIn(): Boolean {
    return id != null
}

fun UserDetail.isDataCompleted(): Boolean {
    return !name.isNullOrEmpty() && !phoneNum.isNullOrEmpty()
}
