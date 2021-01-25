package com.foobarust.data.api

import com.foobarust.data.common.Constants.MAPS_DIRECTIONS_END_POINT
import com.foobarust.data.common.Constants.MAPS_DIRECTIONS_PARAM_DEST
import com.foobarust.data.common.Constants.MAPS_DIRECTIONS_PARAM_KEY
import com.foobarust.data.common.Constants.MAPS_DIRECTIONS_PARAM_ORIGIN
import com.foobarust.data.models.maps.DirectionsResponse
import com.google.android.gms.maps.model.LatLng
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by kevin on 1/4/21
 */

interface MapService {

    /**
     * Get the direction response from Google Direction API
     * @param key google map key
     * @param origin starting place [LatLng] ("latitude+longitude")
     * @param destination [LatLng] destination [LatLng] ("latitude+longitude")
     */
    @GET(MAPS_DIRECTIONS_END_POINT)
    suspend fun getDirections(
        @Query(MAPS_DIRECTIONS_PARAM_KEY) key: String,
        @Query(MAPS_DIRECTIONS_PARAM_ORIGIN) origin: String,
        @Query(MAPS_DIRECTIONS_PARAM_DEST) destination: String
    ): DirectionsResponse
}