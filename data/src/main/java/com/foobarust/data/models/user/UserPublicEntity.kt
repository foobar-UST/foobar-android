package com.foobarust.data.models.user

import com.foobarust.data.common.Constants.USER_ID_FIELD
import com.foobarust.data.common.Constants.USER_PHOTO_URL_FIELD
import com.foobarust.data.common.Constants.USER_USERNAME_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 12/27/20
 */

data class UserPublicEntity(
    @JvmField
    @PropertyName(USER_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(USER_USERNAME_FIELD)
    val username: String? = null,

    @JvmField
    @PropertyName(USER_PHOTO_URL_FIELD)
    val photoUrl: String? = null
)