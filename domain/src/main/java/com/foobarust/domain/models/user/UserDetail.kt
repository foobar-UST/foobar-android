package com.foobarust.domain.models.user

import java.util.*


/**
 * Created by kevin on 9/12/20
 */

// TODO: fix nullable fields
data class UserDetail(
    val name: String? = null,
    val username: String,
    val email: String,
    val phoneNum: String? = null,
    val photoUrl: String? = null,
    val dataCompleted: Boolean,
    val updatedAt: Date?
)

fun UserDetail.isFieldsFulfilledForOrdering(): Boolean {
    return !name.isNullOrEmpty() && !phoneNum.isNullOrEmpty()
}
