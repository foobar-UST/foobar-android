package com.foobarust.domain.usecases.cart

import com.foobarust.domain.di.ApplicationScope
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.user.isSignedIn
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.CartRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import com.foobarust.domain.utils.cancelIfActive
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetUserCartUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, UserCart?>(coroutineDispatcher) {

    private var observeUserCartJob: Job? = null

    override fun execute(parameters: Unit): Flow<Resource<UserCart?>> = channelFlow {
        authRepository.getAuthProfileObservable().collect { result ->
            stopObserveUserCart()
            if (result is Resource.Success) {
                if (result.data.isSignedIn()) {
                    println("[GetUserDetailUseCase]: observing auth: signed in")
                    startObserveUserCart(userId = result.data.id!!)
                } else {
                    // Return null when the user is not signed in
                    println("[GetUserDetailUseCase]: observing auth: signed out, user cart is null")
                    channel.offer(Resource.Success(null))
                }
            }
        }
    }

    private fun ProducerScope<Resource<UserCart>>.startObserveUserCart(userId: String) {
        println("[GetUserCartUseCase]: startObserveUserCart")
        observeUserCartJob = externalScope.launch(coroutineDispatcher) {
            cartRepository.getUserCartObservable(userId).collect {
                when (it) {
                    is Resource.Success -> channel.offer(Resource.Success(it.data))
                    is Resource.Error -> channel.offer(Resource.Error(it.message))
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    private fun stopObserveUserCart() {
        println("[GetUserCartUseCase]: stopObserveUserCart")
        observeUserCartJob.cancelIfActive()
    }
}