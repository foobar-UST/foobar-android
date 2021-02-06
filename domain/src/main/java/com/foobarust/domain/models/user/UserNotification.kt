package com.foobarust.domain.models.user

import java.util.*

/**
 * Created by kevin on 2/5/21
 */

data class UserNotification(
    val id: String,
    val title: String,
    val body: String,
    val link: String,
    val imageUrl: String?,
    val createdAt: Date
)