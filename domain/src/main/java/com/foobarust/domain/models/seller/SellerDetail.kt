package com.foobarust.domain.models.seller

import com.foobarust.domain.models.common.Geolocation

/**
 * Created by kevin on 9/27/20
 */

data class SellerDetail(
    val id: String,
    val name: String,
    val nameZh: String?,
    val description: String?,
    val descriptionZh: String?,
    val phoneNum: String,
    val website: String?,
    val location: Geolocation,
    val imageUrl: String?,
    val minSpend: Double,
    val rating: Double,
    val ratingCount: Int,
    val type: SellerType,
    val online: Boolean,
    val notice: String?,
    val openingHours: String,
    val tags: List<String>
)

fun SellerDetail.getNormalizedName(): String {
    return if (nameZh != null) "$name $nameZh" else name
}

fun SellerDetail.getNormalizedDescription(): String? {
    return when {
        description != null && descriptionZh != null -> "$description\n\n$descriptionZh"
        description != null -> description
        descriptionZh != null -> descriptionZh
        else -> null
    }
}

fun SellerDetail.getNormalizedAddress(): String {
    return "${location.address}\n${location.addressZh}"
}

fun SellerDetail.getNormalizedRatingString(): String {
    return if (rating == 0.0) "n.a." else String.format("%.1f", rating)
}