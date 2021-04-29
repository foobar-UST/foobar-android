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

        when (parameters) {
            is UpdateUserDetailParameters.UpdateName -> {
                // Check for empty name
                val name = parameters.name

                if (name.isBlank()) {
                    throw Exception("Name is empty.")
                }

                userRepository.updateUserDetail(
                    idToken = idToken,
                    name = name,
                    phoneNum = null
                )

            }
            is UpdateUserDetailParameters.UpdatePhoneNum -> {
                // Check for invalid phone number length
                val phoneNum = parameters.phoneNum

                if (phoneNum.isBlank() ||
                    phoneNum.length != parameters.defaultPhoneNumLength) {
                    throw Exception("Invalid phone num length.")
                }

                userRepository.updateUserDetail(
                    idToken = idToken,
                    name = null,
                    phoneNum = phoneNum
                )
            }
        }

        emit(Resource.Success(Unit))
    }
}

sealed class UpdateUserDetailParameters {
    data class UpdateName(val name: String) : UpdateUserDetailParameters()
    data class UpdatePhoneNum(
        val phoneNum: String,
        val defaultPhoneNumLength: Int
    ) : UpdateUserDetailParameters()
}