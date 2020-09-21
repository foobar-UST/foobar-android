package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.AuthProfile
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 9/20/20
 */

private const val GET_USER_BASIC_INFO_NOT_SIGNED_IN = "User is not signed in."

class GetAuthProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, AuthProfile>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<AuthProfile>> = flow {
        // Check if user is signed in
        if (!authRepository.isSignedIn()) {
            emit(Resource.Error(GET_USER_BASIC_INFO_NOT_SIGNED_IN))
            return@flow
        }

        emitAll(authRepository.getAuthProfileObservable())
    }
}