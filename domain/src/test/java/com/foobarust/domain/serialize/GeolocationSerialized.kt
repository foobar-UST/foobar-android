package com.foobarust.domain.serialize

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

@Serializable
internal data class GeoPointSerialized(
    val lat: Double,
    val long: Double
)

