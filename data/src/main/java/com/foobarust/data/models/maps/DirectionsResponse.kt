package com.foobarust.data.models.maps

import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 1/5/21
 */

data class DirectionsResponse(
    @SerializedName("overview_polyline")
    val encodedPoints: String
)