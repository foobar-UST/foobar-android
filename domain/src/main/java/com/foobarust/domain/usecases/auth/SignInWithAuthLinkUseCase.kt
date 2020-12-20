package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 8/26/20
 */

class SignInWithAuthLinkUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): CoroutineUseCase<SignInWithAuthLinkParameters, Unit>(dispatcher) {

    override suspend fun execute(parameters: SignInWithAuthLinkParameters) {
        authRepository.signInWithEmailLink(
            email = parameters.email,
            emailLink = parameters.authLink
        )
    }
}

data class SignInWithAuthLinkParameters(
    val email: String,
    val authLink: String
)