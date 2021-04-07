package com.foobarust.domain.repositories

import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.map.TravelMode

/**
 * Created by kevin on 1/4/21
 */

interface MapRepository {

    suspend fun getDirectionsPath(
        currentLocation: GeolocationPoint,
        destination: GeolocationPoint,
        travelMode: TravelMode
    ): List<GeolocationPoint>

    fun getStaticMapImageUrl(centerLocation: GeolocationPoint): String
}