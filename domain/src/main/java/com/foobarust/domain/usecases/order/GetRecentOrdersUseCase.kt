package com.foobarust.domain.usecases.order

import com.foobarust.domain.common.UseCaseExceptions.ERROR_USER_NOT_SIGNED_IN
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.OrderRepository
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

/**
 * Created by kevin on 1/29/21
 */

private const val TAG = "GetRecentOrdersUseCase"

class GetRecentOrdersUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<OrderBasic>>(coroutineDispatcher) {

    private var observeRecentOrdersJob: Job? = null

    private val sharedResult: Flow<Resource<List<OrderBasic>>> = channelFlow {
        val coroutineScope = CoroutineScope(currentCoroutineContext())

        authRepository.authProfileObservable.collect {
            stopObserveRecentOrders()

            when (it) {
                is AuthState.Authenticated -> {
                    println("[$TAG]: User is signed in. Start observe recent order items.")
                    startObserveRecentOrders(coroutineScope, it.data.id)
                }
                AuthState.Unauthenticated -> {
                    println("[$TAG]: User is signed out.")
                    channel.offer(Resource.Success(emptyList()))
                    channel.offer(Resource.Error(ERROR_USER_NOT_SIGNED_IN))
                }
                AuthState.Loading -> {
                    println("[$TAG]: Loading auth profile...")
                    channel.offer(Resource.Loading())
                }
            }
        }
    }

    override fun execute(parameters: Unit): Flow<Resource<List<OrderBasic>>> = sharedResult

    private fun sortByOrderState(orderBasics: List<OrderBasic>): List<OrderBasic> {
        return orderBasics.sortedByDescending { it.state.precedence }
    }

    private fun ProducerScope<Resource<List<OrderBasic>>>.startObserveRecentOrders(
        coroutineScope: CoroutineScope,
        userId: String
    ) {
        observeRecentOrdersJob = coroutineScope.launch(coroutineDispatcher) {
            orderRepository.getActiveOrderItemsObservable(userId).collect {
                when (it) {
                    is Resource.Success -> {
                        println("[$TAG]: Offered recent order items.")
                        channel.offer(Resource.Success(sortByOrderState(it.data)))
                    }
                    is Resource.Error -> {
                        // Failed to receive order items
                        println("[$TAG]: Failed to receive order items: ${it.message}")
                        channel.offer(Resource.Error(it.message))
                    }
                    is Resource.Loading -> {
                        channel.offer(Resource.Loading())
                    }
                }
            }
        }
    }

    private fun stopObserveRecentOrders() {
        println("[$TAG]: Stop observing recent order items.")
        observeRecentOrdersJob.cancelIfActive()
    }
}