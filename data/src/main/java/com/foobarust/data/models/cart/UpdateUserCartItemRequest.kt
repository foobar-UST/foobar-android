package com.foobarust.data.models.cart

import com.foobarust.data.common.Constants.UPDATE_USER_CART_ITEM_REQUEST_AMOUNTS
import com.foobarust.data.common.Constants.UPDATE_USER_CART_ITEM_REQUEST_CART_ITEM_ID
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 12/15/20
 */

data class UpdateUserCartItemRequest(
    @SerializedName(UPDATE_USER_CART_ITEM_REQUEST_CART_ITEM_ID)
    val cartItemId: String,

    @SerializedName(UPDATE_USER_CART_ITEM_REQUEST_AMOUNTS)
    val amounts: Int
)