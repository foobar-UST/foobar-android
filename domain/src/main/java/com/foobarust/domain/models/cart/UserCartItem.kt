package com.foobarust.domain.models.cart

import java.util.*

/**
 * Created by kevin on 12/1/20
 */
data class UserCartItem(
    val id: String,
    val itemId: String,
    val itemSellerId: String,
    val itemTitle: String,
    val itemTitleZh: String?,
    val itemPrice: Double,
    val itemImageUrl: String?,
    val amounts: Int,
    val totalPrice: Double,
    val updatePriceRequired: Boolean,
    val updatedAt: Date?
)

fun UserCartItem.getNormalizedTitle(): String {
    return if (itemTitleZh != null) "$itemTitle\n$itemTitleZh" else itemTitle
}