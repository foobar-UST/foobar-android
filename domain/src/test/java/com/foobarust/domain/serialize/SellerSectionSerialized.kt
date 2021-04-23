package com.foobarust.domain.serialize

import com.foobarust.domain.models.seller.SellerSectionBasic
import com.foobarust.domain.models.seller.SellerSectionDetail
import com.foobarust.domain.models.seller.SellerSectionState
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kevin on 4/20/21
 */

@Serializable
internal data class SellerSectionSerialized(
    val id: String,
    val title: String,
    val title_zh: String? = null,
    val group_id: String,
    val seller_id: String,
    val seller_name: String,
    val seller_name_zh: String? = null,
    val delivery_cost: Double,
    val delivery_time: String,
    val delivery_location: GeolocationSerialized,
    val description: String,
    val description_zh: String? = null,
    val cutoff_time: String,
    val max_users: Int,
    val joined_users_count: Int? = 0,
    val joined_users_ids: List<String>? = emptyList(),
    val image_url: String? = null,
    val state: String,
    val available: Boolean
)

internal fun SellerSectionSerialized.toSellerSectionDetail(): SellerSectionDetail {
    return SellerSectionDetail(
        id = id, title = title, titleZh = title_zh, groupId = group_id, sellerId = seller_id,
        sellerName = seller_name, sellerNameZh = seller_name_zh, deliveryCost = delivery_cost,
        deliveryTime = parseTimestamp(delivery_time),
        deliveryLocation = delivery_location.toGeolocation(),
        description = description, descriptionZh = description_zh,
        cutoffTime = parseTimestamp(cutoff_time),
        maxUsers = max_users, joinedUsersCount = joined_users_count ?: 0,
        joinedUsersIds = joined_users_ids ?: emptyList(),
        imageUrl = image_url,
        available = available,
        state = parseSectionState(state)
    )
}

internal fun SellerSectionSerialized.toSellerSectionBasic(): SellerSectionBasic {
    return SellerSectionBasic(
        id = id, title = title, titleZh = title_zh, sellerId = seller_id, sellerName = seller_name,
        sellerNameZh = seller_name_zh, deliveryTime = parseTimestamp(delivery_time),
        cutoffTime = parseTimestamp(cutoff_time),
        maxUsers = max_users, joinedUsersCount = joined_users_count ?: 0,
        imageUrl = image_url,
        available = available,
        state = parseSectionState(state)
    )
}

private fun parseSectionState(state: String): SellerSectionState {
    return when (state) {
        "0_available" -> SellerSectionState.AVAILABLE
        "1_processing" -> SellerSectionState.PROCESSING
        "2_preparing" -> SellerSectionState.PREPARING
        "3_shipped" -> SellerSectionState.SHIPPED
        "4_ready_for_pick_up" -> SellerSectionState.READY_FOR_PICK_UP
        "5_delivered" -> SellerSectionState.DELIVERED
        else -> throw IllegalArgumentException("Invalid section state.")
    }
}

private fun parseTimestamp(time: String): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    formatter.timeZone = TimeZone.getTimeZone("Asia/Hong_Kong")
    return formatter.parse(time)
}