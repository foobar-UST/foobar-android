package com.foobarust.data.repositories

import com.foobarust.data.BuildConfig.GOOGLE_MAPS_API_KEY
import com.foobarust.data.api.MapService
import com.foobarust.data.constants.Constants.MAPS_API_URL
import com.foobarust.data.constants.Constants.MAPS_STATIC_MAP_END_POINT
import com.foobarust.data.constants.Constants.MAPS_STATIC_MAP_PARAM_AUTO_SCALE
import com.foobarust.data.constants.Constants.MAPS_STATIC_MAP_PARAM_FORMAT
import com.foobarust.data.constants.Constants.MAPS_STATIC_MAP_PARAM_KEY
import com.foobarust.data.constants.Constants.MAPS_STATIC_MAP_PARAM_MARKERS
import com.foobarust.data.constants.Constants.MAPS_STATIC_MAP_PARAM_SIZE
import com.foobarust.data.constants.Constants.MAPS_STATIC_MAP_PARAM_VISUAL_REFRESH
import com.foobarust.data.mappers.MapMapper
import com.foobarust.data.utils.asGeolocationPoint
import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.map.TravelMode
import com.foobarust.domain.repositories.MapRepository
import com.google.maps.android.PolyUtil
import javax.inject.Inject

/**
 * Created by kevin on 1/4/21
 */

class MapRepositoryImpl @Inject constructor(
    private val mapService: MapService,
    private val mapMapper: MapMapper
) : MapRepository {

    override suspend fun getDirectionsPath(
        currentLocation: GeolocationPoint,
        destination: GeolocationPoint,
        travelMode: TravelMode
    ): List<GeolocationPoint> {
        val response = mapService.getDirections(
            key = GOOGLE_MAPS_API_KEY,
            origin = "${currentLocation.latitude},${currentLocation.longitude}",
            destination = "${destination.latitude},${destination.longitude}",
            travelMode = mapMapper.fromTravelMode(travelMode)
        )

        return PolyUtil.decode(response.encodedPoints)
            .map { it.asGeolocationPoint() }
    }

    override fun getStaticMapImageUrl(centerLocation: GeolocationPoint): String {
        return MAPS_API_URL +
            "$MAPS_STATIC_MAP_END_POINT?" +
            "$MAPS_STATIC_MAP_PARAM_KEY=$GOOGLE_MAPS_API_KEY&" +
            "$MAPS_STATIC_MAP_PARAM_AUTO_SCALE=1&" +
            "$MAPS_STATIC_MAP_PARAM_SIZE=1920x1080&" +
            "$MAPS_STATIC_MAP_PARAM_FORMAT=png&" +
            "$MAPS_STATIC_MAP_PARAM_VISUAL_REFRESH=true&" +
            "$MAPS_STATIC_MAP_PARAM_MARKERS=${centerLocation.latitude},${centerLocation.longitude}"
    }
}