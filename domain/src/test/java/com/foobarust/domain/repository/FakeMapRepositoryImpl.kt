package com.foobarust.domain.repository

import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.map.TravelMode
import com.foobarust.domain.repositories.MapRepository

/**
 * Created by kevin on 4/19/21
 */

class FakeMapRepositoryImpl : MapRepository {

    override suspend fun getDirectionsPath(
        currentLocation: GeolocationPoint,
        destination: GeolocationPoint,
        travelMode: TravelMode
    ): List<GeolocationPoint> {
        return listOf(
            GeolocationPoint(1.0, 2.0),
            GeolocationPoint(2.0, 3.0)
        )
    }

    override fun getStaticMapImageUrl(centerLocation: GeolocationPoint): String {
        return "about:blank"
    }
}