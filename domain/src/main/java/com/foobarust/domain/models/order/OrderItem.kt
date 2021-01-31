package com.foobarust.domain.models.order

/**
 * Created by kevin on 1/28/21
 */

data class OrderItem(
    val id: String,
    val itemId: String,
    val itemSellerId: String,
    val itemTitle: String,
    val itemTitleZh: String?,
    val itemPrice: Double,
    val itemImageUrl: String?,
    val amounts: Int,
    val totalPrice: Double
)

fun OrderItem.getNormalizedTitle(): String {
    return if (itemTitleZh != null) "$itemTitle\n$itemTitleZh" else itemTitle
}
