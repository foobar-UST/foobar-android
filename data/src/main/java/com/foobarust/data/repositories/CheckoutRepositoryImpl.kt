package com.foobarust.data.repositories

import com.foobarust.data.common.Constants.PAYMENT_METHODS_COLLECTION
import com.foobarust.data.mappers.CheckoutMapper
import com.foobarust.data.utils.getAwaitResult
import com.foobarust.domain.models.checkout.PaymentMethod
import com.foobarust.domain.repositories.CheckoutRepository
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

/**
 * Created by kevin on 1/9/21
 */

class CheckoutRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val checkoutMapper: CheckoutMapper
) : CheckoutRepository {

    override suspend fun getPaymentMethods(): List<PaymentMethod> {
        return firestore.collection(PAYMENT_METHODS_COLLECTION)
            .getAwaitResult(checkoutMapper::toPaymentMethod)
    }
}