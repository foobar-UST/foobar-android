package com.foobarust.data.db

import androidx.room.*
import com.foobarust.data.models.order.OrderDetailCacheDto
import com.foobarust.data.models.order.OrderDetailWithItemsCacheDto
import com.foobarust.data.models.order.OrderItemCacheDto

/**
 * Created by kevin on 2/28/21
 */

@Dao
interface OrdersDetailDao {

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderDetailWithItems(orderId: String): OrderDetailWithItemsCacheDto

    @Transaction
    suspend fun insertOrderDetailWithItems(
        orderDetailCacheDto: OrderDetailCacheDto,
        orderItemCacheDtos: List<OrderItemCacheDto>
    ) {
        insertOrderDetail(orderDetailCacheDto)
        insertOrderItems(orderItemCacheDtos)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderDetail(orderDetailCacheDto: OrderDetailCacheDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(orderItemCacheDtos: List<OrderItemCacheDto>)

    @Transaction
    suspend fun deleteAll() {
        deleteAllOrders()
        deleteAllOrderItems()
    }

    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()

    @Query("DELETE FROM order_items")
    suspend fun deleteAllOrderItems()
}