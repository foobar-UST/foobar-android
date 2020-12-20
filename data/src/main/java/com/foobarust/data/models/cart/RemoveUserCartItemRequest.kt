package com.foobarust.data.models.cart

import com.foobarust.data.common.Constants.REMOVE_USER_CART_ITEM_REQUEST_CART_ITEM_ID
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 12/15/20
 */

data class RemoveUserCartItemRequest(
    @SerializedName(REMOVE_USER_CART_ITEM_REQUEST_CART_ITEM_ID)
    val cartItemId: String
)