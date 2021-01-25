package com.foobarust.domain.models.cart

/**
 * Created by kevin on 1/19/21
 */

data class AddUserCartItem(
    val itemId: String,
    val amounts: Int,
    val sectionId: String? = null
)