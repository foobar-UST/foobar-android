package com.foobarust.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.foobarust.data.models.user.UserDetailCacheDto
import com.foobarust.data.models.user.UserNotificationCacheDto

/**
 * Created by kevin on 1/23/21
 */

@Database(
    entities = [UserDetailCacheDto::class, UserNotificationCacheDto::class],
    version = 2
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDetailDao(): UserDetailDao
    abstract fun userNotificationDao(): UserNotificationDao
}