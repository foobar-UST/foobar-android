package com.foobarust.domain.repositories

import androidx.paging.PagingData
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.models.order.OrderDetail
import com.foobarust.domain.models.order.OrderRating
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 1/29/21
 */

interface OrderRepository {

    fun getActiveOrderItemsObservable(userId: String): Flow<Resource<List<OrderBasic>>>

    fun getHistoryOrderItemsPagingData(userId: String): Flow<PagingData<OrderBasic>>

    fun getOrderDetailObservable(orderId: String): Flow<Resource<OrderDetail>>

    suspend fun submitOrderRating(idToken: String, orderRating: OrderRating)

    suspend fun removeOrderItemsCache()

    suspend fun removeOrderDetailsCache()
}