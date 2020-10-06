package com.foobarust.data.models

import com.foobarust.data.common.Constants.ADVERTISE_CONTENT_FIELD
import com.foobarust.data.common.Constants.ADVERTISE_CREATED_AT_FIELD
import com.foobarust.data.common.Constants.ADVERTISE_ID_FIELD
import com.foobarust.data.common.Constants.ADVERTISE_IMAGE_URL_FIELD
import com.foobarust.data.common.Constants.ADVERTISE_SELLER_ID_FIELD
import com.foobarust.data.common.Constants.ADVERTISE_SELLER_NAME_FIELD
import com.foobarust.data.common.Constants.ADVERTISE_TITLE_FIELD
import com.foobarust.data.common.Constants.ADVERTISE_TYPE_FIELD
import com.google.firebase.firestore.PropertyName
import java.sql.Timestamp

/**
 * Created by kevin on 9/28/20
 *
 * Represent a document structure in 'advertises' collection.
 */

data class AdvertiseDetailEntity(
    @JvmField
    @PropertyName(ADVERTISE_ID_FIELD) val id: String? = null,

    @JvmField
    @PropertyName(ADVERTISE_SELLER_ID_FIELD) val sellerId: String? = null,

    @JvmField
    @PropertyName(ADVERTISE_SELLER_NAME_FIELD) val sellerName: String? = null,

    @JvmField
    @PropertyName(ADVERTISE_TITLE_FIELD) val title: String? = null,

    @JvmField
    @PropertyName(ADVERTISE_CONTENT_FIELD) val content: String? = null,

    @JvmField
    @PropertyName(ADVERTISE_TYPE_FIELD) val type: String? = null,

    @JvmField
    @PropertyName(ADVERTISE_CREATED_AT_FIELD) val createdAt: Timestamp? = null,

    @JvmField
    @PropertyName(ADVERTISE_IMAGE_URL_FIELD) val imageUrl: String? = null
)