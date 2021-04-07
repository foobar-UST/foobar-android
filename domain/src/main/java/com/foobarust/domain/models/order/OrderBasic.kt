package com.foobarust.domain.models.order

import java.util.*

/**
 * Created by kevin on 1/29/21
 */

data class OrderBasic(
    val id: String,
    val title: String,
    val titleZh: String?,
    val sellerId: String,
    val sellerName: String,
    val sellerNameZh: String?,
    val sectionId: String?,
    val identifier: String,
    val imageUrl: String?,
    val type: OrderType,
    val orderItemsCount: Int,
    val state: OrderState,
    val deliveryAddress: String,
    val deliveryAddressZh: String?,
    val totalCost: Double,
    val createdAt: Date,
    val updatedAt: Date
)

fun OrderBasic.getNormalizedTitle(): String {
    return if (titleZh != null) "$title $titleZh" else title
}

fun OrderBasic.getNormalizedSellerName(): String {
    return if (sellerNameZh != null) "$sellerName $sellerNameZh" else sellerName
}

fun OrderBasic.getNormalizedDeliveryAddress(): String {
    return if (deliveryAddressZh != null) {
        "$deliveryAddress $deliveryAddressZh"
    } else {
        deliveryAddress
    }
}
