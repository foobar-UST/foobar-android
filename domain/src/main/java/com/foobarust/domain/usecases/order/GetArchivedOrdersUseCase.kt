package com.foobarust.domain.usecases.order

import androidx.paging.PagingData
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.OrderRepository
import com.foobarust.domain.usecases.PagingUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Created by kevin on 1/30/21
 */

class GetArchivedOrdersUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : PagingUseCase<Unit, OrderBasic>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<PagingData<OrderBasic>> = flow {
        if (!authRepository.isSignedIn()) {
            emitAll(
                flowOf(PagingData.empty<OrderBasic>())
            )
        } else {
            val userId = authRepository.getUserId()
            emitAll(
                orderRepository.getArchivedOrderBasics(userId)
            )
        }
    }
}