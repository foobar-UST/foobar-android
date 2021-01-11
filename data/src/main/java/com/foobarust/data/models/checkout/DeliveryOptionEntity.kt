package com.foobarust.data.models.checkout

import com.foobarust.data.common.Constants.DELIVERY_OPTION_ENABLED_FIELD
import com.foobarust.data.common.Constants.DELIVERY_OPTION_FOR_SELLER_TYPE_FIELD
import com.foobarust.data.common.Constants.DELIVERY_OPTION_IDENTIFIER_FIELD
import com.foobarust.data.common.Constants.DELIVERY_OPTION_ID_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 1/10/21
 */

data class DeliveryOptionEntity(
    @JvmField
    @PropertyName(DELIVERY_OPTION_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(DELIVERY_OPTION_IDENTIFIER_FIELD)
    val identifier: String? = null,

    @JvmField
    @PropertyName(DELIVERY_OPTION_FOR_SELLER_TYPE_FIELD)
    val forSellerType: Int? = null,

    @JvmField
    @PropertyName(DELIVERY_OPTION_ENABLED_FIELD)
    val enabled: Boolean? = null
)