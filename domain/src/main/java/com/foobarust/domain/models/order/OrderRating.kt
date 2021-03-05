package com.foobarust.domain.models.order

/**
 * Created by kevin on 2/24/21
 */

data class OrderRating(
    val orderId: String,
    val orderRating: Int,
    val deliveryRating: Boolean?
)