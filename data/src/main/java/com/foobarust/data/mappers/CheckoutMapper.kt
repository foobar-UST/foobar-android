package com.foobarust.data.mappers

import com.foobarust.data.models.checkout.PaymentMethodDto
import com.foobarust.data.models.checkout.PlaceOrderResponse
import com.foobarust.domain.models.checkout.PaymentMethod
import com.foobarust.domain.models.checkout.PlaceOrderResult
import javax.inject.Inject

/**
 * Created by kevin on 1/9/21
 */

class CheckoutMapper @Inject constructor() {

    fun toPaymentMethod(dto: PaymentMethodDto): PaymentMethod {
        return PaymentMethod(
            id = dto.id!!,
            identifier = dto.identifier!!,
            enabled = dto.enabled ?: false
        )
    }

    fun toPlaceOrderResult(placeOrderResponse: PlaceOrderResponse): PlaceOrderResult {
        return PlaceOrderResult(
            orderId = placeOrderResponse.orderId,
            orderIdentifier = placeOrderResponse.orderIdentifier
        )
    }
}