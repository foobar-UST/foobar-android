package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

/**
 * One-shot countdown timer
 * Input: delay in milliseconds
 * Output: is the timer active
 * */

class CountDownTimerUseCase @Inject constructor(
    @MainDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(parameters: Long): Flow<Boolean> = callbackFlow<Boolean> {
        channel.offer(true)

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                channel.offer(false)
                channel.close()
            }
        }, /* delay */ parameters)

        awaitClose { timer.cancel() }
    }.flowOn(coroutineDispatcher)
}