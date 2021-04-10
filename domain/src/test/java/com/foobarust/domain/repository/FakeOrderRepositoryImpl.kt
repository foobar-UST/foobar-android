package com.foobarust.domain.repository

import androidx.paging.PagingData
import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.order.*
import com.foobarust.domain.repositories.OrderRepository
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.util.*

/**
 * Created by kevin on 4/9/21
 */

@ExperimentalStdlibApi
class FakeOrderRepositoryImpl : OrderRepository {

    val activeOrderItems: List<OrderBasic> = buildActiveOrderItems()
    val historyOrderItems: List<OrderBasic> = buildHistoryOrderItems()

    val allOrderItems: List<OrderBasic> = activeOrderItems + historyOrderItems

    private val orderDetailList: MutableList<OrderDetail> = mutableListOf()

    private var shouldReturnNetworkError = false

    override fun getActiveOrderItemsObservable(userId: String): Flow<Resource<List<OrderBasic>>> = flow {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        emit(Resource.Success(activeOrderItems))
    }

    override fun getHistoryOrderItemsPagingData(userId: String): Flow<PagingData<OrderBasic>> = flow {
        emitAll(flowOf(PagingData.from(historyOrderItems)))
    }

    override fun getOrderDetailObservable(orderId: String): Flow<Resource<OrderDetail>> = flow {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        emit(Resource.Success(orderDetailList.first { it.id == orderId }))
    }

    override suspend fun submitOrderRating(idToken: String, orderRating: OrderRating) {
        TODO("Not yet implemented")
    }

    override suspend fun removeOrderItemsCache() {
        if (shouldReturnNetworkError)
            throw Exception("Network error.")
    }

    override suspend fun removeOrderDetailsCache() {
        if (shouldReturnNetworkError)
            throw Exception("Network error.")
    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    fun setExpectedOrderDetail(orderId: String) {
        orderDetailList.add(buildDefaultOrderDetail(orderId))
        orderDetailList.add(buildDefaultOrderDetail(UUID.randomUUID().toString()))
        orderDetailList.add(buildDefaultOrderDetail(UUID.randomUUID().toString()))
        orderDetailList.add(buildDefaultOrderDetail(UUID.randomUUID().toString()))
    }

    private fun buildActiveOrderItems(): List<OrderBasic> = buildList {
        add(buildDefaultOrderBasic().copy(state = OrderState.PROCESSING))
        add(buildDefaultOrderBasic().copy(state = OrderState.PREPARING))
        add(buildDefaultOrderBasic().copy(state = OrderState.IN_TRANSIT))
        add(buildDefaultOrderBasic().copy(state = OrderState.READY_FOR_PICK_UP))
    }

    private fun buildHistoryOrderItems(): List<OrderBasic> = buildList {
        add(buildDefaultOrderBasic().copy(state = OrderState.DELIVERED))
        add(buildDefaultOrderBasic().copy(state = OrderState.ARCHIVED))
        add(buildDefaultOrderBasic().copy(state = OrderState.CANCELLED))
    }

    private fun buildDefaultOrderBasic(): OrderBasic {
        return OrderBasic(
            id = UUID.randomUUID().toString(),
            title = "Order Title",
            titleZh = "Order Title",
            sellerId = UUID.randomUUID().toString(),
            sellerName = "Order Name",
            sellerNameZh = "Order name",
            sectionId = UUID.randomUUID().toString(),
            identifier = UUID.randomUUID().toString(),
            imageUrl = "about:blank",
            type = OrderType.ON_CAMPUS,
            orderItemsCount = 5,
            state = OrderState.PROCESSING,
            deliveryAddress = "address",
            deliveryAddressZh = "address",
            totalCost = 24.5,
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    private fun buildDefaultOrderDetail(orderId: String): OrderDetail {
        return OrderDetail(
            id = orderId,
            title = "Order Title",
            titleZh = "Order Title",
            sellerId = UUID.randomUUID().toString(),
            sellerName = "Order Name",
            sellerNameZh = "Order name",
            sectionId = UUID.randomUUID().toString(),
            sectionTitle = "Section Title",
            sectionTitleZh = "Section Title",
            delivererId = null,
            delivererLocation = null,
            delivererTravelMode = null,
            identifier = UUID.randomUUID().toString(),
            imageUrl = "about:blank",
            type = OrderType.ON_CAMPUS,
            orderItems = emptyList(),
            orderItemsCount = 0,
            state = OrderState.PROCESSING,
            isPaid = false,
            paymentMethod = "Payment Method",
            message = null,
            deliveryLocation = Geolocation(
                address = "address",
                addressZh = "address_zh",
                locationPoint = GeolocationPoint(0.01, 0.02),
            ),
            subtotalCost = 20.4,
            deliveryCost = 10.0,
            totalCost = 30.4,
            verifyCode = UUID.randomUUID().toString(),
            createdAt = Date(),
            updatedAt = Date()
        )
    }
}