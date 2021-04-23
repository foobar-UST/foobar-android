package com.foobarust.domain.repository

import androidx.paging.PagingData
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.models.order.OrderDetail
import com.foobarust.domain.models.order.OrderRating
import com.foobarust.domain.repositories.OrderRepository
import com.foobarust.domain.serialize.OrderSerialized
import com.foobarust.domain.serialize.toOrderBasic
import com.foobarust.domain.serialize.toOrderDetail
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Created by kevin on 4/9/21
 */

@ExperimentalStdlibApi
class FakeOrderRepositoryImpl : OrderRepository {

    internal val allOrderList: List<OrderSerialized> by lazy {
        deserializeJsonList("orders_fake_data.json")
    }

    internal val activeOrderList: List<OrderSerialized> by lazy {
        allOrderList.filter { it.state !in ORDER_HISTORY_STATES }
    }

    internal val historyOrderList: List<OrderSerialized> by lazy {
        allOrderList.filter { it.state in ORDER_HISTORY_STATES }
    }

    private var shouldReturnNetworkError = false
    private var shouldReturnIOError = false

    override fun getActiveOrderItemsObservable(userId: String): Flow<Resource<List<OrderBasic>>> = flow {
        if (shouldReturnNetworkError) {
            emit(Resource.Error("Network error."))
        } else {
            emit(Resource.Success(activeOrderList.map { it.toOrderBasic() }))
        }
    }

    override fun getHistoryOrderItemsPagingData(userId: String): Flow<PagingData<OrderBasic>> = flow {
        emitAll(flowOf(PagingData.from(historyOrderList.map { it.toOrderBasic() })))
    }

    override fun getOrderDetailObservable(orderId: String): Flow<Resource<OrderDetail>> = flow {
        if (shouldReturnNetworkError) {
            emit(Resource.Error("Network error."))
        } else {
            emit(Resource.Success(
                allOrderList.first { it.id == orderId }.toOrderDetail()
            ))
        }
    }

    override suspend fun submitOrderRating(idToken: String, orderRating: OrderRating) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
    }

    override suspend fun removeOrderItemsCache() {
        if (shouldReturnIOError) throw Exception("IO error.")
    }

    override suspend fun removeOrderDetailsCache() {
        if (shouldReturnIOError) throw Exception("IO error.")
    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    fun setIOError(value: Boolean) {
        shouldReturnIOError = value
    }

    private inline fun<reified T> deserializeJsonList(file: String): List<T> {
        val inputStream = javaClass.classLoader.getResourceAsStream(file)!!
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return Json.decodeFromString(jsonString)
    }

    companion object {
        private val ORDER_HISTORY_STATES = listOf("4_delivered", "5_archived", "6_cancelled")
    }
}