package com.foobarust.domain.models

/**
 * Created by kevin on 9/28/20
 */

data class AdvertiseDetail(
    val id: String,
    val sellerId: String,
    val sellerName: String,
    val title: String,
    val content: String,
    val imageUrl: String? = null
)