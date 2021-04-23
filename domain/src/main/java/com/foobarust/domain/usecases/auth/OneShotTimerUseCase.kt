package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * One-shot countdown timer
 * Input: delay in milliseconds
 * Output: is the timer active
 * */

class OneShotTimerUseCase @Inject constructor(
    @MainDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(parameters: Long): Flow<Boolean> = flow {
        emit(true)
        delay(parameters)
        emit(false)
    }
        .flowOn(coroutineDispatcher)
}