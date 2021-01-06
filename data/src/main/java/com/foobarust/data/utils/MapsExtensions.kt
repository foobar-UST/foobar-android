package com.foobarust.data.utils

import com.foobarust.domain.models.common.Geolocation
import com.google.android.gms.maps.model.LatLng

/**
 * Created by kevin on 1/5/21
 */

fun LatLng.asGeolocation(): Geolocation {
    return Geolocation(latitude = latitude, longitude = longitude)
}