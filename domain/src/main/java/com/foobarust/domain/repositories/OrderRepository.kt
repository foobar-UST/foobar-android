package com.foobarust.domain.repositories

import androidx.paging.PagingData
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.models.order.OrderDetail
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 1/29/21
 */

interface OrderRepository {

    fun getActiveOrderBasics(userId: String): Flow<Resource<List<OrderBasic>>>

    fun getArchivedOrderBasics(userId: String): Flow<PagingData<OrderBasic>>

    suspend fun getOrderDetail(orderId: String): OrderDetail
}