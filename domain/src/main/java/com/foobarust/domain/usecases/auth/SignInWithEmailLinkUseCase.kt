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
 * Created by kevin on 8/26/20
 */

class SignInWithEmailLinkUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<SignInWithEmailLinkParameters, Unit>(dispatcher) {

    override fun execute(
        parameters: SignInWithEmailLinkParameters
    ): Flow<Resource<Unit>> = flow {
        authRepository.signInWithEmailLink(
            email = parameters.email,
            emailLink = parameters.authLink
        )
        emit(Resource.Success(Unit))
    }
}

data class SignInWithEmailLinkParameters(
    val email: String,
    val authLink: String
)