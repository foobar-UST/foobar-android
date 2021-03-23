package com.foobarust.domain.usecases.user

import com.foobarust.domain.common.UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN
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
 * Created by kevin on 3/8/21
 */

private const val TAG = "UploadUserPhotoUseCase"

class UploadUserPhotoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<UploadUserPhotoParameters, Unit>(coroutineDispatcher) {

    override fun execute(parameters: UploadUserPhotoParameters): Flow<Resource<Unit>> = flow {
        val userId = if (!authRepository.isUserSignedIn()) {
            throw Exception(ERROR_USER_NOT_SIGNED_IN)
        } else {
            authRepository.getUserId()
        }

        emitAll(userRepository.uploadUserPhoto(
            userId = userId,
            uri = parameters.photoUri,
            extension = parameters.photoExtension
        ))
    }
}

data class UploadUserPhotoParameters(
    val photoUri: String,
    val photoExtension: String
)