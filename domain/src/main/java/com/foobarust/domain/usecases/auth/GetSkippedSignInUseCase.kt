package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.PreferencesRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 8/30/20
 */

open class GetSkippedSignInUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, Boolean>(coroutineDispatcher) {

    override suspend fun execute(parameters: Unit): Boolean {
        return preferencesRepository.isSkippedSignIn
    }
}