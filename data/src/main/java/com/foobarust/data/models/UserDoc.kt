package com.foobarust.data.models

import com.google.firebase.Timestamp

/**
 * Document object of 'users' collection
 */

data class UserDoc(
    val name: String? = null,
    val email: String? = null,
    val username: String? = null,
    val phone_num: String? = null,
    val photo_url: String? = null,
    val updated_at: Timestamp? = null,
    val allow_order: Boolean? = null
    // TODO: deviceIds: List<String>
)