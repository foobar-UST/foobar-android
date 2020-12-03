package com.foobarust.domain.usecases.user

import com.foobarust.domain.common.UseCaseExceptions
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.user.UserCartItem
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 12/1/20
 */
class GetUserCartItemsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<UserCartItem>>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<List<UserCartItem>>> = flow {
        if (!authRepository.isSignedIn()) {
            throw Exception(UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN)
        }

        val userId = authRepository.getAuthUserId()

        emitAll(userRepository.getUserCartItemsObservable(userId))
    }
}