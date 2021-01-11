package com.foobarust.data.models.checkout

import com.foobarust.data.common.Constants.PAYMENT_METHOD_ENABLED_FIELD
import com.foobarust.data.common.Constants.PAYMENT_METHOD_IDENTIFIER_FIELD
import com.foobarust.data.common.Constants.PAYMENT_METHOD_ID_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 1/9/21
 */

data class PaymentMethodEntity(
    @JvmField
    @PropertyName(PAYMENT_METHOD_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(PAYMENT_METHOD_IDENTIFIER_FIELD)
    val identifier: String? = null,

    @JvmField
    @PropertyName(PAYMENT_METHOD_ENABLED_FIELD)
    val enabled: Boolean? = null
)