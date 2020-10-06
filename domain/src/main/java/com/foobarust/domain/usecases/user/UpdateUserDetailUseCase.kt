package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.UserDetail
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 10/2/20
 */

private const val UPDATE_USER_INFO_NOT_SIGNED_IN = "User is not signed in."

class UpdateUserDetailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<UpdateUserInfoParameter, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: UpdateUserInfoParameter) {
        // Check if user is signed in
        if (!authRepository.isSignedIn()) {
            throw Exception(UPDATE_USER_INFO_NOT_SIGNED_IN)
        }

        userRepository.updateUserDetail(
            userId = authRepository.getAuthUserId(),
            userDetail = UserDetail(name = parameters.name, phoneNum = parameters.phoneNum)
        )
    }
}

data class UpdateUserInfoParameter(
    val name: String? = null,
    val phoneNum: String? = null
)