package com.foobarust.domain.models.cart

/**
 * Created by kevin on 1/19/21
 */

data class UpdateUserCartItem(
    val cartItemId: String,
    val amounts: Int
)