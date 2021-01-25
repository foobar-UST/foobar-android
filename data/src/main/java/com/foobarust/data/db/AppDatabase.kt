package com.foobarust.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.foobarust.data.models.user.UserDetailCacheDto

/**
 * Created by kevin on 1/23/21
 */

@Database(
    entities = [UserDetailCacheDto::class],
    version = 1
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}