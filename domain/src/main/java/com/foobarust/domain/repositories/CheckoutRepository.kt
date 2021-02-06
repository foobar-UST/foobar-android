package com.foobarust.domain.repositories

import com.foobarust.domain.models.checkout.PaymentMethod
import com.foobarust.domain.models.checkout.PlaceOrderResult

/**
 * Created by kevin on 1/9/21
 */

interface CheckoutRepository {

    suspend fun getPaymentMethods(): List<PaymentMethod>

    suspend fun placeOrder(idToken: String, message: String?, paymentMethodIdentifier: String): PlaceOrderResult
}