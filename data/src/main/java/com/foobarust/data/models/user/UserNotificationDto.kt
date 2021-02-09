package com.foobarust.data.models.user

import com.foobarust.data.common.Constants.USER_NOTIFICATION_BODY_FIELD
import com.foobarust.data.common.Constants.USER_NOTIFICATION_CREATED_AT_FIELD
import com.foobarust.data.common.Constants.USER_NOTIFICATION_ID_FIELD
import com.foobarust.data.common.Constants.USER_NOTIFICATION_IMAGE_URL_FIELD
import com.foobarust.data.common.Constants.USER_NOTIFICATION_LINK_FIELD
import com.foobarust.data.common.Constants.USER_NOTIFICATION_TITLE_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 2/5/21
 */

data class UserNotificationDto(
    @JvmField
    @PropertyName(USER_NOTIFICATION_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(USER_NOTIFICATION_TITLE_FIELD)
    val title: String? = null,

    @JvmField
    @PropertyName(USER_NOTIFICATION_BODY_FIELD)
    val body: String? = null,

    @JvmField
    @PropertyName(USER_NOTIFICATION_LINK_FIELD)
    val link: String? = null,

    @JvmField
    @PropertyName(USER_NOTIFICATION_IMAGE_URL_FIELD)
    val imageUrl: String? = null,

    @JvmField
    @PropertyName(USER_NOTIFICATION_CREATED_AT_FIELD)
    val createdAt: Timestamp? = null
)