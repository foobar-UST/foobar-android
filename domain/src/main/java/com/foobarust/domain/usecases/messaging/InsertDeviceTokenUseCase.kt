package com.foobarust.domain.usecases.messaging

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.MessagingRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 2/6/21
 */

class InsertDeviceTokenUseCase @Inject constructor(
    private val messagingRepository: MessagingRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Unit>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<Unit>> = flow {
        val deviceToken = messagingRepository.getDeviceToken()
        messagingRepository.uploadDeviceToken(deviceToken)

        emit(Resource.Success(Unit))
    }
}