package com.foobarust.domain.models.seller

import com.foobarust.domain.utils.DateUtils
import com.foobarust.domain.utils.TimeUtils
import java.util.*

/**
 * Created by kevin on 12/20/20
 */

data class SellerSectionBasic(
    val id: String,
    val title: String,
    val titleZh: String?,
    val sellerId: String,
    val sellerName: String,
    val sellerNameZh: String?,
    val deliveryTime: Date,
    val cutoffTime: Date,
    val maxUsers: Int,
    val joinedUsersCount: Int,
    val imageUrl: String?,
    val state: SellerSectionState,
    val available: Boolean
)

fun SellerSectionBasic.isRecentSection(): Boolean {
    return available &&
        state == SellerSectionState.AVAILABLE &&
        DateUtils.isSameDay(deliveryTime, Date())
}

fun SellerSectionBasic.getSellerNormalizedName(): String {
    return if (sellerNameZh != null) "$sellerName $sellerNameZh" else sellerName
}

fun SellerSectionBasic.getNormalizedTitleForUpcoming(): String {
    val title = if (titleZh != null) "$title $titleZh" else title
    val deliverTimeString = TimeUtils.get12HourString(deliveryTime)
    return "$title - $deliverTimeString"
}

fun SellerSectionBasic.getNormalizedTitleForRecent(): String {
    val title = if (titleZh != null) "$title $titleZh" else title
    val dateString = DateUtils.getDateString(date = deliveryTime, format = "dd/MM")
    val deliveryTimeString = TimeUtils.get12HourString(deliveryTime)
    return "($dateString) $title\n@ $deliveryTimeString"
}

fun SellerSectionBasic.getNormalizedTitleForMoreSections(): String {
    val title = if (titleZh != null) "$title - $titleZh" else title
    val dateString = DateUtils.getDateString(date = deliveryTime, format = "dd/MM")
    return "[$dateString] $title"
}

fun SellerSectionBasic.getDeliveryDateString(): String {
    return DateUtils.getDateString(date = deliveryTime, format = "yyyy-MM-dd")
}

fun SellerSectionBasic.getCutoffTimeString(): String {
    return TimeUtils.get12HourString(cutoffTime)
}

fun SellerSectionBasic.getDeliveryTimeString(): String {
    return TimeUtils.get12HourString(deliveryTime)
}