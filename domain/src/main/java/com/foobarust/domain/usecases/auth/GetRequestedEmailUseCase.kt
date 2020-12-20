package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 8/28/20
 */

class GetRequestedEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, String>(coroutineDispatcher) {

    override suspend fun execute(parameters: Unit): String {
        return authRepository.getAuthRequestedEmail()
    }
}