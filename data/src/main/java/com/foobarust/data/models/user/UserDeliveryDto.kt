package com.foobarust.data.models.user

import com.foobarust.data.constants.Constants.USER_ID_FIELD
import com.foobarust.data.constants.Constants.USER_NAME_FIELD
import com.foobarust.data.constants.Constants.USER_PHONE_NUM_FIELD
import com.foobarust.data.constants.Constants.USER_PHOTO_URL_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 4/5/21
 */

data class UserDeliveryDto(
    @JvmField
    @PropertyName(USER_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(USER_NAME_FIELD)
    val name: String? = null,

    @JvmField
    @PropertyName(USER_PHONE_NUM_FIELD)
    val phoneNum: String? = null,

    @JvmField
    @PropertyName(USER_PHOTO_URL_FIELD)
    val photoUrl: String? = null
)