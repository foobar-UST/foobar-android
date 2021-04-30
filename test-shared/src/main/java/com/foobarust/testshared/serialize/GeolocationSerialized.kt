package com.foobarust.testshared.serialize

import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.common.GeolocationPoint
import kotlinx.serialization.Serializable

/**
 * Created by kevin on 4/19/21
 */

@Serializable
data class GeolocationSerialized(
    val address: String,
    val address_zh: String,
    val geopoint: GeoPointSerialized
)

fun GeolocationSerialized.toGeolocation(): Geolocation {
    return Geolocation(
        address = address, addressZh = address_zh, locationPoint = geopoint.toGeoPoint()
    )
}

@Serializable
data class GeoPointSerialized(
    val lat: Double,
    val long: Double
)

fun GeoPointSerialized.toGeoPoint(): GeolocationPoint {
    return GeolocationPoint(
        latitude = lat, longitude = long
    )
}

