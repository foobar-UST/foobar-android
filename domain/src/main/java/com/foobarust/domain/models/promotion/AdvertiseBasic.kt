package com.foobarust.domain.models.promotion

import java.util.*


/**
 * Created by kevin on 10/3/20
 */

data class AdvertiseBasic(
    val id: String,
    val url: String,
    val imageUrl: String?,
    val createdAt: Date
)