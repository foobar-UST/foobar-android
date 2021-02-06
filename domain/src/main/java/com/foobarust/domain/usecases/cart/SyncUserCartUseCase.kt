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

/**
 * Created by kevin on 12/19/20
 */

class SyncUserCartUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Unit>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<Unit>> = flow {
        if (!authRepository.isUserSignedIn()) {
            throw Exception(UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN)
        }

        val idToken = authRepository.getUserIdToken()

        cartRepository.syncUserCart(idToken)

        emit(Resource.Success(Unit))
    }
}