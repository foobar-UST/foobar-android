package com.foobarust.data.models.order

import com.foobarust.data.constants.Constants.ORDER_BASIC_DELIVERY_ADDRESS_FIELD
import com.foobarust.data.constants.Constants.ORDER_BASIC_DELIVERY_ADDRESS_ZH_FIELD
import com.foobarust.data.constants.Constants.ORDER_CRATED_AT_FIELD
import com.foobarust.data.constants.Constants.ORDER_IDENTIFIER_FIELD
import com.foobarust.data.constants.Constants.ORDER_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.ORDER_SECTION_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_SELLER_ID_FIELD
import com.foobarust.data.constants.Constants.ORDER_STATE_FIELD
import com.foobarust.data.constants.Constants.ORDER_TITLE_FIELD
import com.foobarust.data.constants.Constants.ORDER_TITLE_ZH_FIELD
import com.foobarust.data.constants.Constants.ORDER_TOTAL_COST_FIELD
import com.foobarust.data.constants.Constants.ORDER_TYPE_FIELD
import com.foobarust.data.constants.Constants.ORDER_UPDATED_AT_FIELD
import com.foobarust.data.constants.Constants.ORDER_USER_ID_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 1/28/21
 */

data class OrderBasicDto(
    @JvmField
    @PropertyName(ORDER_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(ORDER_TITLE_FIELD)
    val title: String? = null,

    @JvmField
    @PropertyName(ORDER_TITLE_ZH_FIELD)
    val titleZh: String? = null,

    @JvmField
    @PropertyName(ORDER_USER_ID_FIELD)
    val userId: String? = null,

    @JvmField
    @PropertyName(ORDER_SELLER_ID_FIELD)
    val sellerId: String? = null,

    @JvmField
    @PropertyName(ORDER_SECTION_ID_FIELD)
    val sectionId: String? = null,

    @JvmField
    @PropertyName(ORDER_IDENTIFIER_FIELD)
    val identifier: String? = null,

    @JvmField
    @PropertyName(ORDER_IMAGE_URL_FIELD)
    val imageUrl: String? = null,

    @JvmField
    @PropertyName(ORDER_TYPE_FIELD)
    val type: Int? = null,

    @JvmField
    @PropertyName(ORDER_STATE_FIELD)
    val state: String? = null,

    @JvmField
    @PropertyName(ORDER_BASIC_DELIVERY_ADDRESS_FIELD)
    val deliveryAddress: String? = null,

    @JvmField
    @PropertyName(ORDER_BASIC_DELIVERY_ADDRESS_ZH_FIELD)
    val deliveryAddressZh: String? = null,

    @JvmField
    @PropertyName(ORDER_TOTAL_COST_FIELD)
    val totalCost: Double? = null,


    @JvmField
    @PropertyName(ORDER_CRATED_AT_FIELD)
    val createdAt: Timestamp? = null,

    @JvmField
    @PropertyName(ORDER_UPDATED_AT_FIELD)
    val updatedAt: Timestamp? = null
)