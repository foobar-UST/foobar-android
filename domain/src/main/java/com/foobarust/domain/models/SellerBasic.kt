package com.foobarust.domain.models

import com.foobarust.domain.utils.TimeUtil

/**
 * Created by kevin on 9/27/20
 */

data class SellerBasic(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String? = null,
    val openTime: String,
    val closeTime: String,
    val minSpend: Double? = null,
    val rating: Double,
    val type: SellerType
)

fun SellerBasic.isOpening(): Boolean {
    return TimeUtil.isCurrentTimeWithinRange(
        startTime = openTime,
        endTime = closeTime
    )
}
