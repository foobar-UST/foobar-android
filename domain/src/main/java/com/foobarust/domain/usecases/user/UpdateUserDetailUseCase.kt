package com.foobarust.domain.usecases.user

import com.foobarust.domain.common.UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 10/2/20
 */

class UpdateUserDetailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<UserDetail, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: UserDetail) {
        // Check if user is signed in
        if (!authRepository.isSignedIn()) {
            throw Exception(ERROR_USER_NOT_SIGNED_IN)
        }

        userRepository.updateRemoteUserDetail(
            userId = authRepository.getAuthUserId(),
            userDetail = parameters
        )
    }
}