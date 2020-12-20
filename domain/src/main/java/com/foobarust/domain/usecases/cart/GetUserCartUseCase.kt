package com.foobarust.domain.usecases.cart

import com.foobarust.domain.common.UseCaseExceptions
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.CartRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserCartUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, UserCart>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<UserCart>> = flow {
        if (!authRepository.isSignedIn()) {
            throw Exception(UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN)
        }

        val userId = authRepository.getUserId()

        emitAll(cartRepository.getUserCartObservable(userId))
    }
}