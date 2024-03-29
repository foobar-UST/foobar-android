package com.foobarust.android.shared

import com.foobarust.domain.models.common.GeolocationPoint
import java.util.concurrent.TimeUnit

/**
 * Created by kevin on 4/5/21
 */



object AppConfig {
    val CART_TIMEOUT = TimeUnit.MINUTES.toMillis(30)

    const val MAP_ROUTE_WIDTH = 15f
    const val MAP_ZOOM_LEVEL = 15f

    const val PHONE_NUM_PREFIX = "+852"
    const val PHONE_NUM_LENGTH = 8

    val HKUST_LOCATION = GeolocationPoint(22.33776, 114.26364)
}