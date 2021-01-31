package com.foobarust.domain.models.order

import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.utils.DateUtils
import java.util.*

/**
 * Created by kevin on 1/28/21
 */

data class OrderDetail(
    val id: String,
    val title: String,
    val titleZh: String?,
    val sellerId: String,
    val sectionId: String?,
    val delivererId: String?,
    val identifier: String,
    val imageUrl: String?,
    val type: OrderType,
    val orderItems: List<OrderItem>,
    val state: OrderState,
    val isPaid: Boolean,
    val paymentMethod: String,
    val message: String?,
    val deliveryLocation: Geolocation,
    val subtotalCost: Double,
    val deliveryCost: Double,
    val totalCost: Double,
    val createdAt: Date,
    val updatedAt: Date
)

fun OrderDetail.getNormalizedTitle(): String {
    return if (titleZh != null) "$title $titleZh" else title
}

fun OrderDetail.getCreatedAtString(): String {
    return DateUtils.getDateString(date = createdAt, format = "yyyy-MM-dd HH:mm")
}

fun OrderDetail.getUpdatedAtString(): String {
    return DateUtils.getDateString(date = createdAt, format = "yyyy-MM-dd HH:mm")
}