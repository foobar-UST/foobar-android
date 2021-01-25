package com.foobarust.domain.models.cart

import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.utils.DateUtils
import com.foobarust.domain.utils.TimeUtils
import java.util.*

data class UserCart(
    val userId: String,
    val title: String,
    val titleZh: String?,
    val sellerId: String,
    val sellerType: SellerType,
    val sectionId: String?,
    val deliveryTime: Date?,
    val imageUrl: String?,
    val pickupLocation: Geolocation,
    val itemsCount: Int,
    val subtotalCost: Double,
    val deliveryCost: Double,
    val totalCost: Double,
    val syncRequired: Boolean,
    val updatedAt: Date?
)

fun UserCart.getNormalizedTitle(): String {
    return if (titleZh != null) "$title $titleZh" else title
}

fun UserCart.getDeliveryDateString(): String? {
    return deliveryTime?.let {
        DateUtils.getDateString(date = it, format = "yyyy-MM-dd")
    }
}

fun UserCart.getDeliveryTimeString(): String? {
    return deliveryTime?.let {
        TimeUtils.get12HourString(it)
    }
}

fun UserCart.getNormalizedPickupAddress(): String {
    val (address, addressZh) = pickupLocation
    return if (addressZh != null) "$address $addressZh" else address
}