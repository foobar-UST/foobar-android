package com.foobarust.data.mappers

import com.foobarust.data.models.checkout.PaymentMethodEntity
import com.foobarust.domain.models.checkout.PaymentMethod
import javax.inject.Inject

/**
 * Created by kevin on 1/9/21
 */

class CheckoutMapper @Inject constructor() {

    fun toPaymentMethod(entity: PaymentMethodEntity): PaymentMethod {
        return PaymentMethod(
            id = entity.id!!,
            identifier = entity.identifier!!,
            enabled = entity.enabled ?: false
        )
    }
}