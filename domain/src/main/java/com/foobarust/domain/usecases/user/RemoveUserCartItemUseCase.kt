package com.foobarust.domain.usecases.user

import com.foobarust.domain.common.UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.user.UserCartItem
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 12/1/20
 */
class RemoveUserCartItemUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<UserCartItem, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: UserCartItem) {
        if (!authRepository.isSignedIn()) {
            throw Exception(ERROR_USER_NOT_SIGNED_IN)
        }

        val userId = authRepository.getAuthUserId()

        userRepository.removeUserCartItem(
            userId = userId,
            userCartItem = parameters
        )
    }
}