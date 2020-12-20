package com.foobarust.domain.usecases.cart

import com.foobarust.domain.common.UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN
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
 * Created by kevin on 12/1/20
 */
class AddUserCartItemUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<AddUserCartItemParameters, Unit>(coroutineDispatcher) {

    override fun execute(parameters: AddUserCartItemParameters): Flow<Resource<Unit>> = flow {
        if (!authRepository.isSignedIn()) {
            throw Exception(ERROR_USER_NOT_SIGNED_IN)
        }

        val idToken = authRepository.getIdToken()
        val result = cartRepository.addUserCartItem(
            idToken = idToken,
            sellerId = parameters.sellerId,
            itemId = parameters.itemId,
            amounts = parameters.amounts
        )

        when (result) {
            is Resource.Success -> emit(Resource.Success(result.data))
            is Resource.Error -> emit(Resource.Error(result.message))
            is Resource.Loading -> emit(Resource.Loading())
        }
    }
}

data class AddUserCartItemParameters(
    val sellerId: String,
    val itemId: String,
    val amounts: Int
)