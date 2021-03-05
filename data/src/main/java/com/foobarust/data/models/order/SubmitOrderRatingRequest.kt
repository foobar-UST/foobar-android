package com.foobarust.data.models.order

import com.foobarust.data.constants.Constants.SUBMIT_ORDER_RATING_DELIVERY_RATING
import com.foobarust.data.constants.Constants.SUBMIT_ORDER_RATING_ORDER_ID
import com.foobarust.data.constants.Constants.SUBMIT_ORDER_RATING_ORDER_RATING
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 2/24/21
 */

data class SubmitOrderRatingRequest(
    @SerializedName(SUBMIT_ORDER_RATING_ORDER_ID)
    val orderId: String,

    @SerializedName(SUBMIT_ORDER_RATING_ORDER_RATING)
    val orderRating: Int,

    @SerializedName(SUBMIT_ORDER_RATING_DELIVERY_RATING)
    val deliveryRating: Boolean?
)