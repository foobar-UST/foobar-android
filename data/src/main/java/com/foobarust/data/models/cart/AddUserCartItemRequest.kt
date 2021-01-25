package com.foobarust.data.models.cart

import com.foobarust.data.common.Constants.ADD_USER_CART_ITEM_REQUEST_AMOUNTS
import com.foobarust.data.common.Constants.ADD_USER_CART_ITEM_REQUEST_ITEM_ID
import com.foobarust.data.common.Constants.ADD_USER_CART_ITEM_REQUEST_SECTION_ID
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 12/14/20
 */

data class AddUserCartItemRequest(

    @SerializedName(ADD_USER_CART_ITEM_REQUEST_ITEM_ID)
    val itemId: String,

    @SerializedName(ADD_USER_CART_ITEM_REQUEST_AMOUNTS)
    val amounts: Int,

    @SerializedName(ADD_USER_CART_ITEM_REQUEST_SECTION_ID)
    val sectionId: String?
)