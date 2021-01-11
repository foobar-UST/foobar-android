package com.foobarust.domain.repositories

import com.foobarust.domain.models.checkout.DeliveryOption
import com.foobarust.domain.models.checkout.PaymentMethod
import com.foobarust.domain.models.seller.SellerType

/**
 * Created by kevin on 1/9/21
 */

interface CheckoutRepository {

    suspend fun getPaymentMethods(): List<PaymentMethod>

    suspend fun getDeliveryOptions(sellerType: SellerType): List<DeliveryOption>
}