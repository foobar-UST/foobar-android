package com.foobarust.data.models

/**
 * Created by kevin on 9/1/20
 */

data class User(

    val uid: String,

    val username: String,

    val email: String,

    val photoUrl: String,

    val deviceIds: List<String>

)