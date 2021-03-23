package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.DefaultDispatcher
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import javax.inject.Inject

/**
 * One-shot countdown timer
 * Input: delay in milliseconds
 * Output: is the timer active
 *
 * Created by kevin on 1/22/21
 */

class CountDownTimerUseCase @Inject constructor(
    @DefaultDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Long, Boolean>(coroutineDispatcher) {

    override fun execute(parameters: Long): Flow<Resource<Boolean>> = callbackFlow {
        channel.offer(Resource.Success(true))
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                channel.offer(Resource.Success(false))
                channel.close()
            }
        }, /* delay */ parameters)

        awaitClose { timer.cancel() }
    }
}