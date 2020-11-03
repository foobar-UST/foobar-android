package com.foobarust.domain.models

/**
 * Created by kevin on 10/11/20
 */

data class SellerLocation(
    val address: String,
    val geoLocation: GeoLocation
)