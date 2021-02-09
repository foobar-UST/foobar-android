package com.foobarust.data.models.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.foobarust.data.common.Constants.USERS_CACHE_ENTITY
import com.foobarust.data.common.Constants.USER_EMAIL_FIELD
import com.foobarust.data.common.Constants.USER_ID_FIELD
import com.foobarust.data.common.Constants.USER_NAME_FIELD
import com.foobarust.data.common.Constants.USER_PHONE_NUM_FIELD
import com.foobarust.data.common.Constants.USER_PHOTO_URL_FIELD
import com.foobarust.data.common.Constants.USER_UPDATED_AT_FIELD
import com.foobarust.data.common.Constants.USER_USERNAME_FIELD
import java.util.*

@Entity(tableName = USERS_CACHE_ENTITY)
data class UserDetailCacheDto(
    @PrimaryKey
    @ColumnInfo(name = USER_ID_FIELD)
    val id: String,

    @ColumnInfo(name = USER_NAME_FIELD)
    val name: String? = null,

    @ColumnInfo(name = USER_USERNAME_FIELD)
    val username: String? = null,

    @ColumnInfo(name = USER_EMAIL_FIELD)
    val email: String? = null,

    @ColumnInfo(name = USER_PHONE_NUM_FIELD)
    val phoneNum: String? = null,

    @ColumnInfo(name = USER_PHOTO_URL_FIELD)
    val photoUrl: String? = null,

    @ColumnInfo(name = USER_UPDATED_AT_FIELD)
    val updatedAt: Date? = null
)