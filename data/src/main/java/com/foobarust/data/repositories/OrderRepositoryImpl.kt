package com.foobarust.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.foobarust.data.constants.Constants.ORDERS_BASIC_COLLECTION
import com.foobarust.data.constants.Constants.ORDERS_COLLECTION
import com.foobarust.data.constants.Constants.ORDER_STATE_ARCHIVED
import com.foobarust.data.constants.Constants.ORDER_STATE_CANCELLED
import com.foobarust.data.constants.Constants.ORDER_STATE_DELIVERED
import com.foobarust.data.constants.Constants.ORDER_STATE_FIELD
import com.foobarust.data.constants.Constants.ORDER_USER_ID_FIELD
import com.foobarust.data.mappers.OrderMapper
import com.foobarust.data.paging.HistoryOrderBasicsPagingSource
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.models.order.OrderDetail
import com.foobarust.domain.repositories.OrderRepository
import com.foobarust.domain.states.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by kevin on 1/29/21
 */

private const val ARCHIVED_ORDERS_PAGE_SIZE = 7

class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val orderMapper: OrderMapper
) : OrderRepository {

    override fun getActiveOrderItemsObservable(userId: String): Flow<Resource<List<OrderBasic>>> {
        return firestore.collection(ORDERS_BASIC_COLLECTION)
            .whereEqualTo(ORDER_USER_ID_FIELD, userId)
            .whereNotIn(
                ORDER_STATE_FIELD,
                listOf(ORDER_STATE_ARCHIVED, ORDER_STATE_CANCELLED, ORDER_STATE_DELIVERED)
            )
            .snapshotFlow(orderMapper::toOrderBasic, keepAlive = true)
    }

    override fun getHistoryOrderItemsPagingData(userId: String): Flow<PagingData<OrderBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = ARCHIVED_ORDERS_PAGE_SIZE * 2,
                pageSize = ARCHIVED_ORDERS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { HistoryOrderBasicsPagingSource(firestore) }
        ).flow.map { pagingData ->
            pagingData.map { orderMapper.toOrderBasic(it) }
        }
    }

    override fun getOrderDetailObservable(orderId: String): Flow<Resource<OrderDetail>> {
        return firestore.document("${ORDERS_COLLECTION}/$orderId")
            .snapshotFlow(orderMapper::toOrderDetail)
    }
}