package com.foobarust.domain.models.seller

import com.foobarust.domain.utils.DateUtils
import com.foobarust.domain.utils.TimeUtils
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
    val deliveryTime: Date,
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
        DateUtils.isSameDay(deliveryTime, Date())
}

fun SellerSectionDetail.getNormalizedDescription(): String {
    return if (descriptionZh != null) "$description\n$descriptionZh" else description
}

fun SellerSectionDetail.getCutoffTimeString(): String {
    return TimeUtils.get12HourString(cutoffTime)
}

fun SellerSectionDetail.getDeliveryDateString(): String {
    return DateUtils.getDateString(date = deliveryTime, format = "yyyy-MM-dd")
}

fun SellerSectionDetail.getDeliveryTimeString(): String {
    return TimeUtils.get12HourString(deliveryTime)
}