package com.foobarust.domain.serialize

import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.common.GeolocationPoint
import kotlinx.serialization.Serializable

/**
 * Created by kevin on 4/19/21
 */

@Serializable
internal data class GeolocationSerialized(
    val address: String,
    val address_zh: String,
    val geopoint: GeoPointSerialized
)

internal fun GeolocationSerialized.toGeolocation(): Geolocation {
    return Geolocation(
        address = address, addressZh = address_zh, locationPoint = geopoint.toGeoPoint()
    )
}

@Serializable
internal data class GeoPointSerialized(
    val lat: Double,
    val long: Double
)

internal fun GeoPointSerialized.toGeoPoint(): GeolocationPoint {
    return GeolocationPoint(
        latitude = lat, longitude = long
    )
}

