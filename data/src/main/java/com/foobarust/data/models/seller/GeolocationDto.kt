package com.foobarust.data.models.seller

import com.foobarust.data.constants.Constants.GEO_LOCATION_ADDRESS_FIELD
import com.foobarust.data.constants.Constants.GEO_LOCATION_ADDRESS_ZH_FIELD
import com.foobarust.data.constants.Constants.GEO_LOCATION_GEOPOINT_FIELD
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 10/11/20
 */

data class GeolocationDto(
    @JvmField
    @PropertyName(GEO_LOCATION_ADDRESS_FIELD)
    val address: String? = null,

    @JvmField
    @PropertyName(GEO_LOCATION_ADDRESS_ZH_FIELD)
    val addressZh: String? = null,

    @JvmField
    @PropertyName(GEO_LOCATION_GEOPOINT_FIELD)
    val geopoint: GeoPoint? = null
)