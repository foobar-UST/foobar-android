package com.foobarust.domain.models


/**
 * Created by kevin on 9/12/20
 */

data class UserDetailInfo(
    val username: String? = null,
    val email: String? = null,
    val name: String? = null,
    val phoneNum: String? = null,
    val photoUrl: String? = null
)

fun UserDetailInfo.allowOrdering(): Boolean {
    return !name.isNullOrEmpty() && !phoneNum.isNullOrEmpty()
}