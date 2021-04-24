package com.foobarust.data.mappers

import com.foobarust.data.models.seller.GeolocationDto
import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.common.GeolocationPoint
import com.google.firebase.firestore.GeoPoint

/**
 * Created by kevin on 1/21/21
 */

internal fun GeolocationDto.toGeolocation(): Geolocation {
    return Geolocation(
        address = address!!,
        addressZh = addressZh,
        locationPoint = GeolocationPoint(
            latitude = geopoint?.latitude ?: 0.toDouble(),
            longitude = geopoint?.longitude ?: 0.toDouble()
        )
    )
}

internal fun GeoPoint.toGeolocationPoint(): GeolocationPoint {
    return GeolocationPoint(latitude = latitude, longitude = longitude)
}