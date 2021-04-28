package com.foobarust.domain.serialize

import com.foobarust.domain.models.order.OrderItem
import kotlinx.serialization.Serializable

/**
 * Created by kevin on 4/22/21
 */

@Serializable
internal data class OrderItemSerialized(
    val id: String,
    val item_id: String,
    val item_seller_id: String,
    val item_title: String,
    val item_title_zh: String? = null,
    val item_price: Double,
    val item_image_url: String? = null,
    val amounts: Int,
    val total_price: Double,
    val order_id: String
)

internal fun OrderItemSerialized.toOrderItem(): OrderItem {
    return OrderItem(
        id = id, itemId = item_id, itemTitle = item_title, itemTitleZh = item_title_zh,
        itemPrice = item_price, itemImageUrl = item_image_url, amounts = amounts,
        totalPrice = total_price, itemSellerId = item_seller_id
    )
}