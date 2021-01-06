package com.foobarust.data.api

import com.foobarust.data.common.Constants.GM_DIR_DEST
import com.foobarust.data.common.Constants.GM_DIR_KEY
import com.foobarust.data.common.Constants.GM_DIR_ORIGIN
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
    @GET("directions/json")
    suspend fun getDirections(
        @Query(GM_DIR_KEY) key: String,
        @Query(GM_DIR_ORIGIN) origin: String,
        @Query(GM_DIR_DEST) destination: String
    ): DirectionsResponse
}