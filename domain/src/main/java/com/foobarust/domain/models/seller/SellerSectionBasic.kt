package com.foobarust.domain.models.seller

import com.foobarust.domain.utils.format
import com.foobarust.domain.utils.getTimeBy12Hour
import com.foobarust.domain.utils.isSameDay
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
        deliveryTime.isSameDay(Date())
}

fun SellerSectionBasic.getSellerNormalizedName(): String {
    return if (sellerNameZh != null) "$sellerName $sellerNameZh" else sellerName
}

fun SellerSectionBasic.getNormalizedTitleForUpcoming(): String {
    val title = if (titleZh != null) "$title $titleZh" else title
    val deliverTimeString = deliveryTime.getTimeBy12Hour()
    return "$title - $deliverTimeString"
}

fun SellerSectionBasic.getNormalizedTitleForRecent(): String {
    val title = if (titleZh != null) "$title $titleZh" else title
    val dateString = deliveryTime.format("dd/MM")
    val deliveryTimeString = deliveryTime.getTimeBy12Hour()
    return "($dateString) $title\n@ $deliveryTimeString"
}

fun SellerSectionBasic.getNormalizedTitleForMoreSections(): String {
    val title = if (titleZh != null) "$title - $titleZh" else title
    val dateString = deliveryTime.format("dd/MM")
    return "[$dateString] $title"
}

fun SellerSectionBasic.getDeliveryDateString(): String = deliveryTime.format("yyyy-MM-dd")

fun SellerSectionBasic.getCutoffTimeString(): String = cutoffTime.getTimeBy12Hour()