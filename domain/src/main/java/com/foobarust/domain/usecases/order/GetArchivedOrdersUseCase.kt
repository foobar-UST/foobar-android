package com.foobarust.domain.usecases.order

import androidx.paging.PagingData
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.OrderRepository
import com.foobarust.domain.usecases.AuthState
import com.foobarust.domain.usecases.PagingUseCase
import com.foobarust.domain.utils.cancelIfActive
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

/**
 * Created by kevin on 1/30/21
 */

private const val TAG = "GetArchivedOrdersUseCase"

class GetArchivedOrdersUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : PagingUseCase<Unit, OrderBasic>(coroutineDispatcher) {

    private var producePagingDataJob: Job? = null

    private val archivedOrdersFlow: Flow<PagingData<OrderBasic>> = channelFlow {
        val coroutineScope = CoroutineScope(currentCoroutineContext())

        authRepository.authProfileObservable.collect {
            stopProducePagingData()
            when (it) {
                is AuthState.Authenticated -> {
                    println("[$TAG] User is signed in. Offered paging data.")
                    startProducePagingData(coroutineScope, it.data.id)
                }
                AuthState.Unauthenticated -> {
                    println("[$TAG] User is signed out. Offered empty paging data.")
                    channel.offer(PagingData.empty())
                }
                AuthState.Loading -> {
                    channel.offer(PagingData.empty())
                }
            }
        }
    }

    override fun execute(parameters: Unit): Flow<PagingData<OrderBasic>> = archivedOrdersFlow

    private fun ProducerScope<PagingData<OrderBasic>>.startProducePagingData(
        coroutineScope: CoroutineScope,
        userId: String
    ) {
        producePagingDataJob = coroutineScope.launch(coroutineDispatcher) {
            orderRepository.getHistoryOrderItemsPagingData(userId).collect {
                channel.offer(it)
            }
        }
    }

    private fun stopProducePagingData() {
        println("[$TAG]: Stop producing paging data.")
        producePagingDataJob.cancelIfActive()
    }
}