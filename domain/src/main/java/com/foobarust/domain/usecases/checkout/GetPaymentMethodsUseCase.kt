package com.foobarust.domain.usecases.checkout

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.checkout.PaymentMethod
import com.foobarust.domain.repositories.CheckoutRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 1/9/21
 */

class GetPaymentMethodsUseCase @Inject constructor(
    private val checkoutRepository: CheckoutRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<PaymentMethod>>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<List<PaymentMethod>>> = flow {
        val paymentMethods = checkoutRepository.getPaymentMethods()
        emit(Resource.Success(paymentMethods))
    }
}