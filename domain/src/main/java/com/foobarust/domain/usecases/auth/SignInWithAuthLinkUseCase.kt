package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Sign in
 * Created by kevin on 8/26/20
 */

private const val INVALID_AUTH_LINK = "Authentication link is invalid."
private const val SIGNIN_FAILED = "Sign-in failed."

class SignInWithAuthLinkUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<SignInWithAuthLinkParameters, Unit>(dispatcher) {

    override fun execute(parameters: SignInWithAuthLinkParameters): Flow<Resource<Unit>> = flow {
        // Check if the auth link is valid
        if (!authRepository.checkEmailLinkIsValid(parameters.authLink)) {
            emit(Resource.Error(INVALID_AUTH_LINK))
            return@flow
        }

        // Check if the sign-in is success
        val result = authRepository.signInWithEmailLink(
            email = parameters.email,
            emailLink = parameters.authLink
        )

        if (!result) {
            emit(Resource.Error(SIGNIN_FAILED))
            return@flow
        }

        emit(Resource.Success(Unit))
    }
}

data class SignInWithAuthLinkParameters(
    val email: String,
    val authLink: String
)