package com.foobarust.data.models.cart

import com.foobarust.data.common.Constants.USER_CART_ITEMS_AMOUNTS_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_AVAILABLE_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_ID_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_ITEM_ID_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_ITEM_IMAGE_URL_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_ITEM_PRICE_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_ITEM_SELLER_ID_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_ITEM_TITLE_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_ITEM_TITLE_ZH_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_TOTAL_PRICE_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_UPDATED_AT_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

/**
 * Created by kevin on 12/1/20
 */
data class UserCartItemEntity(
    @JvmField
    @PropertyName(USER_CART_ITEMS_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(USER_CART_ITEMS_ITEM_ID_FIELD)
    val itemId: String? = null,

    @JvmField
    @PropertyName(USER_CART_ITEMS_ITEM_SELLER_ID_FIELD)
    val itemSellerId: String? = null,

    @JvmField
    @PropertyName(USER_CART_ITEMS_ITEM_TITLE_FIELD)
    val itemTitle: String? = null,

    @JvmField
    @PropertyName(USER_CART_ITEMS_ITEM_TITLE_ZH_FIELD)
    val itemTitleZh: String? = null,

    @JvmField
    @PropertyName(USER_CART_ITEMS_ITEM_PRICE_FIELD)
    val itemPrice: Double? = null,

    @JvmField
    @PropertyName(USER_CART_ITEMS_ITEM_IMAGE_URL_FIELD)
    val itemImageUrl: String? = null,

    @JvmField
    @PropertyName(USER_CART_ITEMS_AMOUNTS_FIELD)
    val amounts: Int? = null,

    @JvmField
    @PropertyName(USER_CART_ITEMS_TOTAL_PRICE_FIELD)
    val totalPrice: Double? = null,

    @JvmField
    @PropertyName(USER_CART_ITEMS_AVAILABLE_FIELD)
    val available: Boolean? = null,

    @JvmField
    @ServerTimestamp
    @PropertyName(USER_CART_ITEMS_UPDATED_AT_FIELD)
    val updatedAt: Timestamp? = null
)