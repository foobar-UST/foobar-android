package com.foobarust.data.mappers

import com.foobarust.data.models.checkout.DeliveryOptionEntity
import com.foobarust.data.models.checkout.PaymentMethodEntity
import com.foobarust.domain.models.checkout.DeliveryOption
import com.foobarust.domain.models.checkout.PaymentMethod
import com.foobarust.domain.models.seller.SellerType
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

    fun toDeliveryOption(entity: DeliveryOptionEntity): DeliveryOption {
        return DeliveryOption(
            id = entity.id!!,
            identifier = entity.identifier!!,
            forSellerType = SellerType.values()[entity.forSellerType!!],
        )
    }
}