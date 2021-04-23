package com.foobarust.domain.models.user

import java.util.*

/**
 * Created by kevin on 9/12/20
 */

data class UserDetail(
    val id: String,
    val username: String,
    val email: String,
    val name: String? = null,
    val phoneNum: String? = null,
    val photoUrl: String? = null,
    val updatedAt: Date? = null
)

fun UserDetail.isProfileCompleted(): Boolean {
    return !name.isNullOrEmpty() && !phoneNum.isNullOrEmpty()
}
