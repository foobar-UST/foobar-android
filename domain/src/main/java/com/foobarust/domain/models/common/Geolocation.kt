package com.foobarust.domain.models.common

/**
 * Created by kevin on 10/11/20
 */

data class Geolocation(
    val address: String,
    val addressZh: String?,
    val locationPoint: GeolocationPoint
)

data class GeolocationPoint(
    val latitude: Double,
    val longitude: Double
)
