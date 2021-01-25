package com.foobarust.data.models.checkout

import com.foobarust.data.common.Constants.PLACE_ORDER_REQUEST_MESSAGE
import com.foobarust.data.common.Constants.PLACE_ORDER_REQUEST_PAYMENT_METHOD
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 1/17/21
 */

data class PlaceOrderRequest(
    @SerializedName(PLACE_ORDER_REQUEST_MESSAGE)
    val message: String?,

    @SerializedName(PLACE_ORDER_REQUEST_PAYMENT_METHOD)
    val paymentMethod: String
)