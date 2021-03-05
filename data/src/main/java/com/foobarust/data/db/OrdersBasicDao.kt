package com.foobarust.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foobarust.data.constants.Constants.ORDER_STATE_ARCHIVED
import com.foobarust.data.constants.Constants.ORDER_STATE_CANCELLED
import com.foobarust.data.constants.Constants.ORDER_STATE_DELIVERED
import com.foobarust.data.models.order.OrderBasicCacheDto

/**
 * Created by kevin on 2/28/21
 */

@Dao
interface OrdersBasicDao {

    @Query("""
        SELECT * FROM orders_basic
        WHERE state NOT IN ("$ORDER_STATE_ARCHIVED", "$ORDER_STATE_CANCELLED", "$ORDER_STATE_DELIVERED")
    """)
    suspend fun getActiveOrders(): List<OrderBasicCacheDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orderBasicCacheDto: List<OrderBasicCacheDto>)

    @Query("DELETE FROM orders_basic")
    suspend fun deleteAll()
}