package com.foobarust.domain.models.cart

import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.utils.format
import com.foobarust.domain.utils.getTimeBy12Hour
import java.util.*

data class UserCart(
    val userId: String,
    val title: String,
    val titleZh: String?,
    val sellerId: String,
    val sellerName: String,
    val sellerNameZh: String?,
    val sellerType: SellerType,
    val sectionId: String?,
    val sectionTitle: String?,
    val sectionTitleZh: String?,
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

fun UserCart.hasItems(): Boolean = itemsCount > 0

fun UserCart.getNormalizedTitle(): String {
    return if (titleZh != null) "$title $titleZh" else title
}

fun UserCart.getNormalizedSellerName(): String {
    return if (sellerNameZh != null) "$sellerName $sellerNameZh" else sellerName
}

fun UserCart.getNormalizedSectionTitle(): String? {
    return if (sectionTitleZh != null) "$sectionTitle $sectionTitleZh" else sectionTitle
}

fun UserCart.getDeliveryDateString(): String? {
    return deliveryTime?.format("yyyy-MM-dd")
}

fun UserCart.getDeliveryTimeString(): String? {
    return deliveryTime?.getTimeBy12Hour()
}

fun UserCart.getNormalizedPickupAddress(): String {
    val (address, addressZh) = pickupLocation
    return if (addressZh != null) "$address $addressZh" else address
}