package com.foobarust.domain.models.order

import com.foobarust.domain.utils.DateUtils
import java.util.*

/**
 * Created by kevin on 1/29/21
 */

data class OrderBasic(
    val id: String,
    val title: String,
    val titleZh: String?,
    val sellerId: String,
    val sectionId: String?,
    val identifier: String,
    val imageUrl: String?,
    val type: OrderType,
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

fun OrderBasic.getNormalizedDeliveryAddress(): String {
    return if (deliveryAddressZh != null) {
        "$deliveryAddress $deliveryAddressZh"
    } else {
        deliveryAddress
    }
}

fun OrderBasic.getNormalizedTotalCost(): String {
    return String.format("%.1f", totalCost)
}

fun OrderBasic.getCreatedAtString(): String {
    return DateUtils.getDateString(date = createdAt, format = "yyyy-MM-dd HH:mm")
}

fun OrderBasic.getUpdatedAtString(): String {
    return DateUtils.getDateString(date = createdAt, format = "HH:mm")
}
