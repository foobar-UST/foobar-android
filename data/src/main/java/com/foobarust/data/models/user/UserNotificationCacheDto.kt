package com.foobarust.data.models.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.foobarust.data.common.Constants.USER_NOTIFICATIONS_ENTITY
import com.foobarust.data.common.Constants.USER_NOTIFICATION_BODY_FIELD
import com.foobarust.data.common.Constants.USER_NOTIFICATION_CREATED_AT_FIELD
import com.foobarust.data.common.Constants.USER_NOTIFICATION_ID_FIELD
import com.foobarust.data.common.Constants.USER_NOTIFICATION_IMAGE_URL_FIELD
import com.foobarust.data.common.Constants.USER_NOTIFICATION_LINK_FIELD
import com.foobarust.data.common.Constants.USER_NOTIFICATION_TITLE_FIELD
import java.util.*

/**
 * Created by kevin on 2/5/21
 */

@Entity(tableName = USER_NOTIFICATIONS_ENTITY)
data class UserNotificationCacheDto(
    @PrimaryKey
    @ColumnInfo(name = USER_NOTIFICATION_ID_FIELD)
    val id: String,

    @ColumnInfo(name = USER_NOTIFICATION_TITLE_FIELD)
    val title: String? = null,

    @ColumnInfo(name = USER_NOTIFICATION_BODY_FIELD)
    val body: String? = null,

    @ColumnInfo(name = USER_NOTIFICATION_LINK_FIELD)
    val link: String? = null,

    @ColumnInfo(name = USER_NOTIFICATION_IMAGE_URL_FIELD)
    val imageUrl: String? = null,

    @ColumnInfo(name = USER_NOTIFICATION_CREATED_AT_FIELD)
    val createdAt: Date? = null
)