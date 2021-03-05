package com.foobarust.domain.usecases.auth

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.MessagingRepository
import com.foobarust.domain.repositories.OrderRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 9/12/20
 */

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val messagingRepository: MessagingRepository,
    private val orderRepository: OrderRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Unit>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<Unit>> = flow {
        // Remove user detail cache
        userRepository.removeUserDetailCache()

        // Remove orders cache
        orderRepository.removeOrderItemsCache()
        orderRepository.removeOrderDetailsCache()

        // Unlink user from device token
        val deviceToken = messagingRepository.getDeviceToken()
        messagingRepository.unlinkDeviceTokenFromUser(deviceToken)

        authRepository.signOut()

        emit(Resource.Success(Unit))
    }
}