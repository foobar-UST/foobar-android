package com.foobarust.data.utils

import com.foobarust.domain.models.common.GeolocationPoint
import com.google.android.gms.maps.model.LatLng

/**
 * Created by kevin on 1/5/21
 */

fun LatLng.asGeolocationPoint(): GeolocationPoint {
    return GeolocationPoint(
        latitude = latitude,
        longitude = longitude
    )
}