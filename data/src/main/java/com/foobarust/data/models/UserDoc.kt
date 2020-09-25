package com.foobarust.data.models

import java.util.*

/**
 * Document object of 'users' collection
 */

data class UserDoc(
    val name: String? = null,
    val email: String? = null,
    val username: String? = null,
    val phone_num: String? = null,
    val photo_url: String? = null,
    val updated_at: Date? = null,
    val allow_order: Boolean? = null
    // TODO: deviceIds: List<String>
)