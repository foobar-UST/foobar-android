package com.foobarust.domain.models.user

import java.util.*

/**
 * Created by kevin on 2/5/21
 */

data class UserNotification(
    val id: String,
    val titleLocKey: String,
    val titleLocArgs: List<String>,
    val bodyLocKey: String,
    val bodyLocArgs: List<String>,
    val link: String,
    val imageUrl: String?,
    val createdAt: Date
)