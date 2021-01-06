package com.foobarust.domain.usecases.cart

import com.foobarust.domain.common.UseCaseExceptions
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.CartRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ClearUserCartUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Unit>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<Unit>> = flow {
        if (!authRepository.isSignedIn()) {
            throw Exception(UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN)
        }

        val idToken = authRepository.getIdToken()

        cartRepository.clearUserCart(idToken)

        emit(Resource.Success(Unit))
    }
}