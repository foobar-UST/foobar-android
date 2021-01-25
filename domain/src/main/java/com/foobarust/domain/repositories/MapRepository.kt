package com.foobarust.domain.repositories

import com.foobarust.domain.models.common.GeolocationPoint

/**
 * Created by kevin on 1/4/21
 */

interface MapRepository {

    suspend fun getDirectionsPath(
        originLatitude: Double, originLongitude: Double,
        destLatitude: Double, destLongitude: Double
    ): List<GeolocationPoint>

    fun getStaticMapImageUrl(latitude: Double, longitude: Double): String
}