package com.foobarust.domain.usecases.checkout

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.checkout.PlaceOrderResult
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.CheckoutRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 1/26/21
 */

class PlaceOrderUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val checkoutRepository: CheckoutRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<PlaceOrderParameters, PlaceOrderResult>(coroutineDispatcher) {

    override fun execute(parameters: PlaceOrderParameters): Flow<Resource<PlaceOrderResult>> = flow {
        val result = checkoutRepository.placeOrder(
            idToken = authRepository.getIdToken(),
            message = parameters.orderMessage,
            paymentMethodIdentifier = parameters.paymentMethodIdentifier
        )

        emit(Resource.Success(result))
    }
}

data class PlaceOrderParameters(
    val orderMessage: String?,
    val paymentMethodIdentifier: String
)