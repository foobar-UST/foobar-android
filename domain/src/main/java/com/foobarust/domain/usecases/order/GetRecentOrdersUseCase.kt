package com.foobarust.domain.usecases.order

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.OrderRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by kevin on 1/29/21
 */

class GetRecentOrdersUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<OrderBasic>>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<List<OrderBasic>>> = flow {
        val userId = authRepository.getUserId()
        val orderItems = orderRepository.getActiveOrderBasics(userId)
            .map {
                when (it) {
                    is Resource.Success -> Resource.Success(sortByOrderState(it.data))
                    is Resource.Error -> Resource.Error(it.message)
                    is Resource.Loading -> Resource.Loading()
                }
            }

        emitAll(orderItems)
    }

    private fun sortByOrderState(orderBasics: List<OrderBasic>): List<OrderBasic> {
        return orderBasics.sortedByDescending { it.state.priority }
    }
}