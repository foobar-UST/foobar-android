package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class RemoveRequestedEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: Unit) {
        authRepository.removeAuthRequestedEmail()
    }
}