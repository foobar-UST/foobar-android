package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.IoDispatcher
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
 * Created by kevin on Sep, 2020
 */

private const val UPDATE_USER_PHOTO_NOT_SIGNED_IN = "User is not signed in."

class UpdateUserPhotoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, Unit>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<Unit>> = flow {
        // Check if user is signed in
        if (!authRepository.isSignedIn()) {
            emit(Resource.Error(UPDATE_USER_PHOTO_NOT_SIGNED_IN))
            return@flow
        }

        emitAll(userRepository.updateUserPhoto(
            userId = authRepository.getAuthUserId(),
            uriString = parameters
        ))
    }
}