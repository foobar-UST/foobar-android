package com.foobarust.domain.usecases.cart

import com.foobarust.domain.di.ApplicationScope
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.CartRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import com.foobarust.domain.utils.cancelIfActive
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserCartUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, UserCart?>(coroutineDispatcher) {

    private var observeUserCartJob: Job? = null

    // Share result to multiple consumers
    private val sharedResult: SharedFlow<Resource<UserCart?>> = channelFlow<Resource<UserCart?>> {
        authRepository.getAuthProfileObservable().collect { result ->
            println("[GetUserCartUseCase]: AuthProfile collected.")
            stopObserveUserCart()
            if (result is Resource.Success) {
                val authProfile = result.data
                if (authProfile != null) {
                    // User is signed in
                    println("[GetUserCartUseCase]: User is signed in. Start observe UserCart.")
                    startObserveUserCart(userId = result.data.id)
                } else {
                    // User is signed out
                    println("[GetUserCartUseCase]: User is signed out.")
                    channel.offer(Resource.Success(null))
                }
            }
        }
    }.shareIn(
        scope = externalScope,
        started = SharingStarted.WhileSubscribed(),
        replay = 1
    )

    override fun execute(parameters: Unit): Flow<Resource<UserCart?>> = sharedResult

    private fun ProducerScope<Resource<UserCart>>.startObserveUserCart(userId: String) {
        observeUserCartJob = externalScope.launch(coroutineDispatcher) {
            cartRepository.getUserCartObservable(userId).collect {
                when (it) {
                    is Resource.Success -> {
                        println("[GetUserCartUseCase]: Offered UserCart.")
                        channel.offer(Resource.Success(it.data))
                    }
                    is Resource.Error -> {
                        println("[GetUserCartUseCase]: Error getting UserCart: ${it.message}.")
                        channel.offer(Resource.Error(it.message))
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun stopObserveUserCart() {
        println("[GetUserCartUseCase]: Stop observing UserCart from db.")
        observeUserCartJob.cancelIfActive()
    }
}