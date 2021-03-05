package com.foobarust.domain.usecases.cart

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.CartRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.AuthState
import com.foobarust.domain.usecases.FlowUseCase
import com.foobarust.domain.utils.cancelIfActive
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

private const val TAG = "GetUserCartUseCase"

class GetUserCartUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, UserCart>(coroutineDispatcher) {

    private var observeUserCartJob: Job? = null

    private val sharedResult: Flow<Resource<UserCart>> = channelFlow {
        authRepository.authProfileObservable.collect {
            stopObserveUserCart()
            when (it) {
                is AuthState.Authenticated -> {
                    println("[$TAG]: User is signed in. Start observe UserCart.")
                    startObserveUserCart(
                        coroutineScope = CoroutineScope(currentCoroutineContext()),
                        userId = it.data.id
                    )
                }
                AuthState.Unauthenticated -> {
                    println("[$TAG]: User is signed out.")
                    channel.offer(Resource.Error(null))
                }
                AuthState.Loading -> {
                    println("[$TAG]: Loading auth profile...")
                    channel.offer(Resource.Loading())
                }
            }
        }
    }

    override fun execute(parameters: Unit): Flow<Resource<UserCart>> = sharedResult

    private fun ProducerScope<Resource<UserCart>>.startObserveUserCart(
        coroutineScope: CoroutineScope,
        userId: String
    ) {
        observeUserCartJob = coroutineScope.launch(coroutineDispatcher) {
            cartRepository.getUserCartObservable(userId).collect {
                when (it) {
                    is Resource.Success -> {
                        println("[$TAG]: Offered UserCart.")
                        channel.offer(Resource.Success(it.data))
                    }
                    is Resource.Error -> {
                        println("[$TAG]: Error getting UserCart: ${it.message}.")
                        channel.offer(Resource.Error(it.message))
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun stopObserveUserCart() {
        println("[$TAG]: Stop observing UserCart.")
        observeUserCartJob.cancelIfActive()
    }
}