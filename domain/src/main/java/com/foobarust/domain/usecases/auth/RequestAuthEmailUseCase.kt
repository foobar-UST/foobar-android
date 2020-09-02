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
 * Sending an authentication link to a user's mailbox
 * Testing is not needed.
 * Created by kevin on 8/26/20
 */

class RequestAuthEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<String, Unit>(dispatcher) {

    override fun execute(parameters: String): Flow<Resource<Unit>> = flow {
        authRepository.sendAuthEmail(parameters)
        emit(Resource.Success(Unit))
    }
}