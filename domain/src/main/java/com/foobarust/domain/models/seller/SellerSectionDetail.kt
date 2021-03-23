package com.foobarust.domain.models.seller

import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.utils.format
import com.foobarust.domain.utils.getTimeBy12Hour
import com.foobarust.domain.utils.isSameDay
import java.util.*

/**
 * Created by kevin on 12/20/20
 */

data class SellerSectionDetail(
    val id: String,
    val title: String,
    val titleZh: String?,
    val groupId: String,
    val sellerId: String,
    val sellerName: String,
    val sellerNameZh: String?,
    val deliveryCost: Double,
    val deliveryTime: Date,
    val deliveryLocation: Geolocation,
    val description: String,
    val descriptionZh: String?,
    val cutoffTime: Date,
    val maxUsers: Int,
    val joinedUsersCount: Int,
    val joinedUsersIds: List<String>,
    val imageUrl: String?,
    val state: SellerSectionState,
    val available: Boolean
)

fun SellerSectionDetail.isRecentSection(): Boolean {
    return available &&
        state == SellerSectionState.AVAILABLE &&
        deliveryTime.isSameDay(Date())
}

fun SellerSectionDetail.getNormalizedTitle(): String {
    return if (titleZh != null) "$title - $titleZh" else title
}

fun SellerSectionDetail.getNormalizedDescription(): String {
    return if (descriptionZh != null) "$description\n$descriptionZh" else description
}

fun SellerSectionDetail.getNormalizedDeliveryAddress(): String {
    val (address, addressZh) = deliveryLocation
    return if (addressZh != null) "$address\n$addressZh" else address
}

fun SellerSectionDetail.getCutoffTimeString(): String = cutoffTime.getTimeBy12Hour()

fun SellerSectionDetail.getDeliveryDateString(): String = deliveryTime.format("yyyy-MM-dd")

fun SellerSectionDetail.getDeliveryTimeString(): String = deliveryTime.getTimeBy12Hour()