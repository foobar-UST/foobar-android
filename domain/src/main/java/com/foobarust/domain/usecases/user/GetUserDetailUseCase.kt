package com.foobarust.domain.usecases.user

import com.foobarust.domain.common.UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.user.UserDetail
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
 * Created by kevin on 9/17/20
 *
 * Get [UserDetail] which contains all the required data about a user,
 * including the username, email, photoUrl, phoneNum, etc.
 *
 * [Resource.Error] will be emitted when there is network error or the document doesn't exist
 * in server.
 */

class GetUserDetailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, UserDetail>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<UserDetail>> = flow {
        if (!authRepository.isSignedIn()) {
            throw Exception(ERROR_USER_NOT_SIGNED_IN)
        }

        val userId = authRepository.getAuthUserId()

        emitAll(userRepository.getRemoteUserDetailObservable(userId))
    }
}