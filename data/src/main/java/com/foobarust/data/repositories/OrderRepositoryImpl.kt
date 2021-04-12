package com.foobarust.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.foobarust.data.api.RemoteService
import com.foobarust.data.cache.networkCacheResource
import com.foobarust.data.constants.Constants.ORDERS_BASIC_COLLECTION
import com.foobarust.data.constants.Constants.ORDERS_COLLECTION
import com.foobarust.data.constants.Constants.ORDER_STATE_ARCHIVED
import com.foobarust.data.constants.Constants.ORDER_STATE_CANCELLED
import com.foobarust.data.constants.Constants.ORDER_STATE_DELIVERED
import com.foobarust.data.constants.Constants.ORDER_STATE_FIELD
import com.foobarust.data.constants.Constants.ORDER_USER_ID_FIELD
import com.foobarust.data.db.OrdersBasicDao
import com.foobarust.data.db.OrdersDetailDao
import com.foobarust.data.mappers.OrderMapper
import com.foobarust.data.paging.HistoryOrderBasicsPagingSource
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.models.order.OrderDetail
import com.foobarust.domain.models.order.OrderRating
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
    private val ordersBasicDao: OrdersBasicDao,
    private val ordersDetailDao: OrdersDetailDao,
    private val remoteService: RemoteService,
    private val orderMapper: OrderMapper
) : OrderRepository {

    override fun getActiveOrderItemsObservable(userId: String): Flow<Resource<List<OrderBasic>>> {
        return networkCacheResource(
            cacheSource = {
                val orderBasicCacheDtos = ordersBasicDao.getActiveOrders()
                orderBasicCacheDtos.map {
                    orderMapper.fromOrderBasicCacheDtoToOrderBasic(it)
                }
            },
            networkSource = {
                firestore.collection(ORDERS_BASIC_COLLECTION)
                    .whereEqualTo(ORDER_USER_ID_FIELD, userId)
                    .whereNotIn(
                        ORDER_STATE_FIELD,
                        listOf(ORDER_STATE_ARCHIVED, ORDER_STATE_CANCELLED, ORDER_STATE_DELIVERED)
                    )
                    .snapshotFlow(orderMapper::fromOrderBasicNetworkDtoToOrderBasic, true)
            },
            updateCache = { orderBasics ->
                val orderBasicCacheDtos = orderBasics.map {
                    orderMapper.toOrderBasicCacheDto(it)
                }
                ordersBasicDao.insertOrders(orderBasicCacheDtos)
            }
        )
    }

    override fun getHistoryOrderItemsPagingData(userId: String): Flow<PagingData<OrderBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = ARCHIVED_ORDERS_PAGE_SIZE * 2,
                pageSize = ARCHIVED_ORDERS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { HistoryOrderBasicsPagingSource(firestore, userId) }
        )
            .flow
            .map { pagingData ->
                pagingData.map { orderMapper.fromOrderBasicNetworkDtoToOrderBasic(it) }
            }
    }

    override fun getOrderDetailObservable(orderId: String): Flow<Resource<OrderDetail>> {
        return networkCacheResource(
            cacheSource = {
                val orderDetailWithItemsCacheDto = ordersDetailDao.getOrderDetailWithItems(orderId)
                orderMapper.fromOrderDetailWithItemsCacheDtoToOrderDetail(orderDetailWithItemsCacheDto)
            },
            networkSource = {
                firestore.document("${ORDERS_COLLECTION}/$orderId")
                    .snapshotFlow(orderMapper::fromOrderDetailNetworkDtoToOrderDetail, true)
            },
            updateCache = {
                ordersDetailDao.insertOrderDetailWithItems(
                    orderDetailCacheDto = orderMapper.toOrderDetailCacheDto(it),
                    orderItemCacheDtos = orderMapper.toOrderItemCacheDtos(it)
                )
            }
        )
    }

    override suspend fun submitOrderRating(idToken: String, orderRating: OrderRating) {
        remoteService.submitOrderRating(
            idToken,
            orderMapper.toSubmitOrderRatingRequest(orderRating)
        )
    }

    override suspend fun removeOrderItemsCache() {
        ordersBasicDao.deleteAll()
    }

    override suspend fun removeOrderDetailsCache() {
        ordersDetailDao.deleteAll()
    }
}