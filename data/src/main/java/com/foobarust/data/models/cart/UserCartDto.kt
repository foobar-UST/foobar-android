package com.foobarust.data.models.cart

import com.foobarust.data.constants.Constants.USER_CART_DELIVERY_COST_FIELD
import com.foobarust.data.constants.Constants.USER_CART_DELIVERY_TIME_FIELD
import com.foobarust.data.constants.Constants.USER_CART_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.USER_CART_ITEMS_COUNT_FIELD
import com.foobarust.data.constants.Constants.USER_CART_PICKUP_LOCATION_FIELD
import com.foobarust.data.constants.Constants.USER_CART_SECTION_ID_FIELD
import com.foobarust.data.constants.Constants.USER_CART_SECTION_TITLE_FIELD
import com.foobarust.data.constants.Constants.USER_CART_SECTION_TITLE_ZH_FIELD
import com.foobarust.data.constants.Constants.USER_CART_SELLER_ID_FIELD
import com.foobarust.data.constants.Constants.USER_CART_SELLER_NAME_FIELD
import com.foobarust.data.constants.Constants.USER_CART_SELLER_NAME_ZH_FIELD
import com.foobarust.data.constants.Constants.USER_CART_SELLER_TYPE_FIELD
import com.foobarust.data.constants.Constants.USER_CART_SUBTOTAL_COST_FIELD
import com.foobarust.data.constants.Constants.USER_CART_SYNC_REQUIRED_FIELD
import com.foobarust.data.constants.Constants.USER_CART_TITLE_FIELD
import com.foobarust.data.constants.Constants.USER_CART_TITLE_ZH_FIELD
import com.foobarust.data.constants.Constants.USER_CART_TOTAL_COST_FIELD
import com.foobarust.data.constants.Constants.USER_CART_UPDATED_AT_FIELD
import com.foobarust.data.constants.Constants.USER_CART_USER_ID_FIELD
import com.foobarust.data.models.seller.GeolocationDto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class UserCartDto(
    @JvmField
    @PropertyName(USER_CART_USER_ID_FIELD)
    val userId: String? = null,

    @JvmField
    @PropertyName(USER_CART_TITLE_FIELD)
    val title: String? = null,

    @JvmField
    @PropertyName(USER_CART_TITLE_ZH_FIELD)
    val titleZh: String? = null,

    @JvmField
    @PropertyName(USER_CART_SELLER_ID_FIELD)
    val sellerId: String? = null,

    @JvmField
    @PropertyName(USER_CART_SELLER_NAME_FIELD)
    val sellerName: String? = null,

    @JvmField
    @PropertyName(USER_CART_SELLER_NAME_ZH_FIELD)
    val sellerNameZh: String? = null,

    @JvmField
    @PropertyName(USER_CART_SELLER_TYPE_FIELD)
    val sellerType: Int? = null,

    @JvmField
    @PropertyName(USER_CART_SECTION_ID_FIELD)
    val sectionId: String? = null,

    @JvmField
    @PropertyName(USER_CART_SECTION_TITLE_FIELD)
    val sectionTitle: String? = null,

    @JvmField
    @PropertyName(USER_CART_SECTION_TITLE_ZH_FIELD)
    val sectionTitleZh: String? = null,

    @JvmField
    @PropertyName(USER_CART_DELIVERY_TIME_FIELD)
    val deliveryTime: Timestamp? = null,

    @JvmField
    @PropertyName(USER_CART_IMAGE_URL_FIELD)
    val imageUrl: String? = null,

    @JvmField
    @PropertyName(USER_CART_PICKUP_LOCATION_FIELD)
    val pickupLocation: GeolocationDto? = null,

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