package com.foobarust.data.models.cart

import com.foobarust.data.common.Constants.USER_CART_DELIVERY_COST_FIELD
import com.foobarust.data.common.Constants.USER_CART_ITEMS_COUNT_FIELD
import com.foobarust.data.common.Constants.USER_CART_SELLER_ID_FIELD
import com.foobarust.data.common.Constants.USER_CART_SELLER_SECTION_ID_FIELD
import com.foobarust.data.common.Constants.USER_CART_SELLER_TYPE_FIELD
import com.foobarust.data.common.Constants.USER_CART_SUBTOTAL_COST_FIELD
import com.foobarust.data.common.Constants.USER_CART_SYNC_REQUIRED_FIELD
import com.foobarust.data.common.Constants.USER_CART_TOTAL_COST_FIELD
import com.foobarust.data.common.Constants.USER_CART_UPDATED_AT_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class UserCartEntity(
    @JvmField
    @PropertyName(USER_CART_SELLER_ID_FIELD)
    val sellerId: String? = null,

    @JvmField
    @PropertyName(USER_CART_SELLER_TYPE_FIELD)
    val sellerType: Int? = null,

    @JvmField
    @PropertyName(USER_CART_SELLER_SECTION_ID_FIELD)
    val sectionId: String? = null,

    @JvmField
    @PropertyName(USER_CART_ITEMS_COUNT_FIELD)
    val itemsCount: Int? = null,

    @JvmField
    @PropertyName(USER_CART_SUBTOTAL_COST_FIELD)
    val subtotalCost: Double? = null,

    @JvmField
    @PropertyName(USER_CART_DELIVERY_COST_FIELD)
    val deliveryCost: Double? = null,

    @JvmField
    @PropertyName(USER_CART_TOTAL_COST_FIELD)
    val totalCost: Double? = null,

    @JvmField
    @PropertyName(USER_CART_SYNC_REQUIRED_FIELD)
    val syncRequired: Boolean? = null,

    @JvmField
    @PropertyName(USER_CART_UPDATED_AT_FIELD)
    val updatedAt: Timestamp? = null
)