package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

private const val UPDATE_USER_INFO_NOT_SIGNED_IN = "User is not signed in."

class UpdateUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<UpdateUserInfoParameter, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: UpdateUserInfoParameter) {
        // Check if user is signed in
        if (!authRepository.isSignedIn()) {
            throw Exception(UPDATE_USER_INFO_NOT_SIGNED_IN)
        }

        val uid = authRepository.getAuthUid()

        // Upload user name
        parameters.name?.let {
            userRepository.updateUserName(uid, it)
        }

        // Upload phone number
        parameters.phoneNum?.let {
            userRepository.updateUserPhoneNumber(uid, it)
        }
    }
}

data class UpdateUserInfoParameter(
    val name: String? = null,
    val phoneNum: String? = null
)