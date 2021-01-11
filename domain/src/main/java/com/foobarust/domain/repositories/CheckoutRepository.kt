package com.foobarust.domain.repositories

import com.foobarust.domain.models.checkout.PaymentMethod

/**
 * Created by kevin on 1/9/21
 */

interface CheckoutRepository {

    suspend fun getPaymentMethods(): List<PaymentMethod>
}