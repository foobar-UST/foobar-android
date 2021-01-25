package com.foobarust.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foobarust.data.common.Constants.USERS_ENTITY
import com.foobarust.data.common.Constants.USER_ID_FIELD
import com.foobarust.data.models.user.UserDetailCacheDto

/**
 * Created by kevin on 1/23/21
 */

@Dao
interface UserDao {

    @Query("SELECT * FROM $USERS_ENTITY WHERE $USER_ID_FIELD=:userId LIMIT 1")
    suspend fun getUser(userId: String): UserDetailCacheDto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userDetailCacheDto: UserDetailCacheDto)

    @Query("DELETE FROM $USERS_ENTITY")
    suspend fun deleteAll()
}