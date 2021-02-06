package com.foobarust.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foobarust.data.common.Constants.USER_NOTIFICATIONS_ENTITY
import com.foobarust.data.models.user.UserNotificationCacheDto

/**
 * Created by kevin on 2/5/21
 */

@Dao
interface UserNotificationDao {

    @Query("SELECT * FROM $USER_NOTIFICATIONS_ENTITY ORDER BY created_at")
    suspend fun getUserNotifications(): List<UserNotificationCacheDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserNotifications(dtos: List<UserNotificationCacheDto>)

    @Query("DELETE FROM $USER_NOTIFICATIONS_ENTITY WHERE id = :notificationId")
    suspend fun delete(notificationId: String)

    @Query("DELETE FROM $USER_NOTIFICATIONS_ENTITY")
    suspend fun deleteAll()
}