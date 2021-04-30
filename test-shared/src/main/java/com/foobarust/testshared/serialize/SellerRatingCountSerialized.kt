package com.foobarust.testshared.serialize

import com.foobarust.domain.models.seller.SellerRatingCount
import kotlinx.serialization.Serializable

/**
 * Created by kevin on 4/20/21
 */

@Serializable
data class SellerRatingCountSerialized(
    val excellent: Int,
    val very_good: Int,
    val good: Int,
    val fair: Int,
    val poor: Int
)

fun SellerRatingCountSerialized.toSellerRatingCount(): SellerRatingCount {
    return SellerRatingCount(
        excellent = excellent, veryGood = very_good, good = good, fair = fair, poor = poor
    )
}