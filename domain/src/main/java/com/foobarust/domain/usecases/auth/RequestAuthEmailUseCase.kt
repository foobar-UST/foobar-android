package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Sending an authentication link to a user's mailbox
 * Testing is not needed.
 * Created by kevin on 8/26/20
 */

class RequestAuthEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
): CoroutineUseCase<String, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: String) {
        authRepository.sendEmailWithSignInLink(parameters)
    }
}