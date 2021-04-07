package com.foobarust.data.mappers

import com.foobarust.data.constants.Constants.MAPS_DIRECTIONS_MODE_DRIVING
import com.foobarust.data.constants.Constants.MAPS_DIRECTIONS_MODE_WALKING
import com.foobarust.domain.models.map.TravelMode
import javax.inject.Inject

/**
 * Created by kevin on 3/22/21
 */

class MapMapper @Inject constructor() {

    fun fromTravelMode(travelMode: TravelMode): String {
        return when (travelMode) {
            TravelMode.DRIVING -> MAPS_DIRECTIONS_MODE_DRIVING
            TravelMode.WALKING -> MAPS_DIRECTIONS_MODE_WALKING
        }
    }
}