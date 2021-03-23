package com.foobarust.data.mappers

import com.foobarust.data.models.seller.GeolocationDto
import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.common.GeolocationPoint

/**
 * Created by kevin on 1/21/21
 */

internal fun GeolocationDto.toGeolocation(): Geolocation {
    return Geolocation(
        address = address!!,
        addressZh = addressZh,
        locationPoint = GeolocationPoint(
            latitude = geoPoint?.latitude ?: 0.toDouble(),
            longitude = geoPoint?.longitude ?: 0.toDouble()
        )
    )
}