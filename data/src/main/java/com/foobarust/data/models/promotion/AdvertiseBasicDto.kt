package com.foobarust.data.models.promotion

import com.foobarust.data.constants.Constants.ADVERTISE_CREATED_AT_FIELD
import com.foobarust.data.constants.Constants.ADVERTISE_ID_FIELD
import com.foobarust.data.constants.Constants.ADVERTISE_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.ADVERTISE_RANDOM_FIELD
import com.foobarust.data.constants.Constants.ADVERTISE_SELLER_TYPE_FIELD
import com.foobarust.data.constants.Constants.ADVERTISE_URL_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 10/3/20
 *
 * Represent a document structure in 'advertises_basic' collection.
 */

data class AdvertiseBasicDto(
    @JvmField
    @PropertyName(ADVERTISE_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(ADVERTISE_URL_FIELD)
    val url: String? = null,

    @JvmField
    @PropertyName(ADVERTISE_IMAGE_URL_FIELD)
    val imageUrl: String? = null,

    @JvmField
    @PropertyName(ADVERTISE_CREATED_AT_FIELD)
    val createdAt: Timestamp? = null,

    @JvmField
    @PropertyName(ADVERTISE_SELLER_TYPE_FIELD)
    val sellerType: Int? = null,

    @JvmField
    @PropertyName(ADVERTISE_RANDOM_FIELD)
    val random: Int? = null
)