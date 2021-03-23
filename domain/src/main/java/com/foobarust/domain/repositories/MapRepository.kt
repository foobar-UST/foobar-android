package com.foobarust.domain.repositories

import com.foobarust.domain.models.common.GeolocationPoint

/**
 * Created by kevin on 1/4/21
 */

interface MapRepository {

    suspend fun getDirectionsPath(
        currentLocation: GeolocationPoint,
        destination: GeolocationPoint
    ): List<GeolocationPoint>

    fun getStaticMapImageUrl(centerLocation: GeolocationPoint): String
}