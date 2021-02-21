package com.foobarust.data.models.user

import com.foobarust.data.common.Constants.USER_CREATED_REST_FIELD
import com.foobarust.data.common.Constants.USER_DEVICE_IDS_FIELD
import com.foobarust.data.common.Constants.USER_EMAIL_FIELD
import com.foobarust.data.common.Constants.USER_ID_FIELD
import com.foobarust.data.common.Constants.USER_NAME_FIELD
import com.foobarust.data.common.Constants.USER_PHONE_NUM_FIELD
import com.foobarust.data.common.Constants.USER_PHOTO_URL_FIELD
import com.foobarust.data.common.Constants.USER_ROLES_FIELD
import com.foobarust.data.common.Constants.USER_UPDATED_AT_FIELD
import com.foobarust.data.common.Constants.USER_USERNAME_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class UserDetailNetworkDto(
    @JvmField
    @PropertyName(USER_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(USER_NAME_FIELD)
    val name: String? = null,

    @JvmField
    @PropertyName(USER_USERNAME_FIELD)
    val username: String? = null,

    @JvmField
    @PropertyName(USER_EMAIL_FIELD)
    val email: String? = null,

    @JvmField
    @PropertyName(USER_PHONE_NUM_FIELD)
    val phoneNum: String? = null,

    @JvmField
    @PropertyName(USER_PHOTO_URL_FIELD)
    val photoUrl: String? = null,

    @JvmField
    @PropertyName(USER_ROLES_FIELD)
    val roles: List<String>? = null,

    @JvmField
    @PropertyName(USER_DEVICE_IDS_FIELD)
    val deviceIds: List<String>? = null,

    @JvmField
    @ServerTimestamp
    @PropertyName(USER_UPDATED_AT_FIELD)
    val updatedAt: Timestamp? = null,

    @JvmField
    @PropertyName(USER_CREATED_REST_FIELD)
    val createdRest: Boolean? = null,
)