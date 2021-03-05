package com.foobarust.domain.usecases.order

import com.foobarust.domain.common.UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.order.OrderRating
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.OrderRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 2/24/21
 */

class SubmitOrderRatingUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<OrderRating, Unit>(coroutineDispatcher) {

    override fun execute(parameters: OrderRating): Flow<Resource<Unit>> = flow {
        val idToken = if (!authRepository.isUserSignedIn()) {
            throw Exception(ERROR_USER_NOT_SIGNED_IN)
        } else {
            authRepository.getUserIdToken()
        }

        orderRepository.submitOrderRating(idToken, parameters)

        emit(Resource.Success(Unit))
    }
}