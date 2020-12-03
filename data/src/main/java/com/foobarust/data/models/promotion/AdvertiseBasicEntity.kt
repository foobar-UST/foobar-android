package com.foobarust.data.models.promotion

import com.foobarust.data.common.Constants.ADVERTISE_ID_FIELD
import com.foobarust.data.common.Constants.ADVERTISE_IMAGE_URL_FIELD
import com.foobarust.data.common.Constants.ADVERTISE_URL_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 10/3/20
 *
 * Represent a document structure in 'advertises_basic' collection.
 */

data class AdvertiseBasicEntity(
    @JvmField
    @PropertyName(ADVERTISE_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(ADVERTISE_URL_FIELD)
    val url: String? = null,

    @JvmField
    @PropertyName(ADVERTISE_IMAGE_URL_FIELD)
    val imageUrl: String? = null
)