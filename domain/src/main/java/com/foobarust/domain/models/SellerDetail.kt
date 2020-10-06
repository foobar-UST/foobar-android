package com.foobarust.domain.models

import com.foobarust.domain.utils.TimeUtil

/**
 * Created by kevin on 9/27/20
 */

data class SellerDetail(
    val id: String,
    val name: String,
    val description: String,
    val email: String,
    val phoneNum: String,
    val location: GeoLocation,
    val address: String,
    val imageUrl: String? = null,
    val openTime: String,
    val closeTime: String,
    val minSpend: Double? = null,
    val rating: Double,
    val catalogs: List<SellerCatalog>,
    val type: SellerType
)

fun SellerDetail.isOpening(): Boolean {
    return TimeUtil.isCurrentTimeWithinRange(
        startTime = openTime,
        endTime = closeTime
    )
}