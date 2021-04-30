package com.foobarust.testshared.repositories

import com.foobarust.domain.models.checkout.PaymentMethod
import com.foobarust.domain.models.checkout.PlaceOrderResult
import com.foobarust.domain.repositories.CheckoutRepository
import java.util.*

/**
 * Created by kevin on 4/19/21
 */

class FakeCheckoutRepositoryImpl(
    private val idToken: String
) : CheckoutRepository {

    val fakePaymentMethods = listOf(
        PaymentMethod(
            id = UUID.randomUUID().toString(),
            identifier = "fake_payment",
            enabled = true
        ),
        PaymentMethod(
            id = UUID.randomUUID().toString(),
            identifier = "fake_payment_2",
            enabled = false
        )
    )

    private var shouldReturnNetworkError = false

    override suspend fun getPaymentMethods(): List<PaymentMethod> {
        if (shouldReturnNetworkError) throw Exception("IO error.")
        return fakePaymentMethods.filter { it.enabled }
    }

    override suspend fun placeOrder(
        idToken: String,
        message: String?,
        paymentMethodIdentifier: String
    ): PlaceOrderResult {
        if (this.idToken != idToken) throw Exception("Invalid id token.")
        if (shouldReturnNetworkError) throw Exception("IO error.")

        fakePaymentMethods.firstOrNull {
            it.identifier == paymentMethodIdentifier
        } ?: throw Exception("Invalid payment method.")

        return PlaceOrderResult(
            orderId = UUID.randomUUID().toString(),
            orderIdentifier = UUID.randomUUID().toString()
        )
    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }
}