package com.foobarust.domain.models.seller

import java.util.*

/**
 * Created by kevin on 3/3/21
 */

data class SellerRatingBasic(
    val id: String,
    val username: String,
    val userPhotoUrl: String?,
    val orderRating: Double,
    val deliveryRating: Boolean?,
    val createdAt: Date
)