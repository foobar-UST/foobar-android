package com.foobarust.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.foobarust.data.models.order.OrderBasicCacheDto
import com.foobarust.data.models.order.OrderDetailCacheDto
import com.foobarust.data.models.order.OrderItemCacheDto
import com.foobarust.data.models.user.UserDetailCacheDto

/**
 * Created by kevin on 1/23/21
 */

@Database(
    entities = [
        UserDetailCacheDto::class,
        OrderBasicCacheDto::class,
        OrderDetailCacheDto::class,
        OrderItemCacheDto::class
   ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDetailDao(): UserDetailDao

    abstract fun ordersBasicDao(): OrdersBasicDao

    abstract fun ordersDetailDao(): OrdersDetailDao
}