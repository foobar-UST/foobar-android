package com.foobarust.domain.models.seller

import com.foobarust.domain.models.common.Geolocation

/**
 * Created by kevin on 10/11/20
 */

data class SellerLocation(
    val address: String,
    val addressZh: String,
    val geolocation: Geolocation
)

fun SellerLocation.getNormalizedAddress(): String {
    return "$address\n$addressZh"
}