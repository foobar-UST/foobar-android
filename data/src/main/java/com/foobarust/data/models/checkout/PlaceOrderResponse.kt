package com.foobarust.data.models.checkout

import com.foobarust.data.constants.Constants.PLACE_ORDER_RESPONSE_ID
import com.foobarust.data.constants.Constants.PLACE_ORDER_RESPONSE_IDENTIFIER
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 1/26/21
 */

data class PlaceOrderResponse(
    @SerializedName(PLACE_ORDER_RESPONSE_ID)
    val orderId: String,

    @SerializedName(PLACE_ORDER_RESPONSE_IDENTIFIER)
    val orderIdentifier: String
)