package com.foobarust.domain.usecases.user

import com.foobarust.domain.common.UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 10/2/20
 */

class UpdateUserDetailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<UpdateUserDetailParameters, Unit>(coroutineDispatcher) {

    override fun execute(parameters: UpdateUserDetailParameters): Flow<Resource<Unit>> = flow {
        val idToken = if (!authRepository.isUserSignedIn()) {
            throw Exception(ERROR_USER_NOT_SIGNED_IN)
        } else {
            authRepository.getUserIdToken()
        }

        userRepository.updateUserDetail(
            idToken = idToken,
            name = parameters.name,
            phoneNum = parameters.phoneNum
        )

        emit(Resource.Success(Unit))
    }
}

data class UpdateUserDetailParameters(
    val name: String? = null,
    val phoneNum: String? = null
)