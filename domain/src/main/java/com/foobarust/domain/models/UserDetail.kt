package com.foobarust.domain.models


/**
 * Created by kevin on 9/12/20
 */

// TODO: fix nullable fields
data class UserDetail(
    val username: String? = null,
    val email: String? = null,
    val name: String? = null,
    val phoneNum: String? = null,
    val photoUrl: String? = null,
    val allowOrder: Boolean = false
)

fun UserDetail.isFieldsFulfilledForOrdering(): Boolean {
    return !name.isNullOrEmpty() && !phoneNum.isNullOrEmpty()
}
