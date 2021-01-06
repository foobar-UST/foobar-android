package com.foobarust.data.repositories

import com.foobarust.data.BuildConfig
import com.foobarust.data.api.MapService
import com.foobarust.data.utils.asGeolocation
import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.repositories.MapRepository
import com.google.maps.android.PolyUtil
import javax.inject.Inject

/**
 * Created by kevin on 1/4/21
 */

class MapRepositoryImpl @Inject constructor(
    private val mapService: MapService
) : MapRepository {

    override suspend fun getDirectionsPath(
        originLatitude: Double,
        originLongitude: Double,
        destLatitude: Double,
        destLongitude: Double
    ): List<Geolocation> {
        val response = mapService.getDirections(
            key = BuildConfig.GOOGLE_MAPS_API_KEY,
            origin = "$originLatitude,$originLongitude",
            destination = "$destLatitude,$destLongitude"
        )

        return PolyUtil.decode(response.encodedPoints)
            .map { it.asGeolocation() }
    }
}