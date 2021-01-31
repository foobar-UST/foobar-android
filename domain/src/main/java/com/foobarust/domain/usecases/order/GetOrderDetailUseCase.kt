package com.foobarust.domain.usecases.order

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.order.OrderDetail
import com.foobarust.domain.repositories.OrderRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 1/29/21
 */

class GetOrderDetailUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, OrderDetail>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<OrderDetail>> = flow {
        val orderDetail = orderRepository.getOrderDetail(orderId = parameters)
        emit(Resource.Success(orderDetail))
    }
}