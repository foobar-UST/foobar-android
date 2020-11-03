package com.foobarust.domain.models

import java.util.*


/**
 * Created by kevin on 9/12/20
 */

// TODO: fix nullable fields
data class UserDetail(
    val username: String,
    val email: String,
    val name: String? = null,
    val phoneNum: String? = null,
    val photoUrl: String? = null,
    val updatedAt: Date?,
    val allowOrder: Boolean
)

fun UserDetail.isFieldsFulfilledForOrdering(): Boolean {
    return !name.isNullOrEmpty() && !phoneNum.isNullOrEmpty()
}
