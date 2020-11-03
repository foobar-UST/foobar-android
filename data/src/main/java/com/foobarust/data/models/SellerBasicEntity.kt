package com.foobarust.data.models

import com.foobarust.data.common.Constants.SELLER_DESCRIPTION_FIELD
import com.foobarust.data.common.Constants.SELLER_ID_FIELD
import com.foobarust.data.common.Constants.SELLER_IMGAE_URL_FIELD
import com.foobarust.data.common.Constants.SELLER_MIN_SPEND_FIELD
import com.foobarust.data.common.Constants.SELLER_NAME_FIELD
import com.foobarust.data.common.Constants.SELLER_ONLINE_FIELD
import com.foobarust.data.common.Constants.SELLER_RATING_FIELD
import com.foobarust.data.common.Constants.SELLER_TYPE_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 9/27/20
 *
 * Represent a document structure in 'sellers_basic' collection.
 */

data class SellerBasicEntity(
    @JvmField
    @PropertyName(SELLER_ID_FIELD) val id: String? = null,

    @JvmField
    @PropertyName(SELLER_NAME_FIELD) val name: String? = null,

    @JvmField
    @PropertyName(SELLER_IMGAE_URL_FIELD) val imageUrl: String? = null,

    @JvmField
    @PropertyName(SELLER_DESCRIPTION_FIELD) val description: String? = null,

    @JvmField
    @PropertyName(SELLER_RATING_FIELD) val rating: Double? = null,

    @JvmField
    @PropertyName(SELLER_TYPE_FIELD) val type: Int? = null,

    @JvmField
    @PropertyName(SELLER_ONLINE_FIELD) val online: Boolean? = null,

    @JvmField
    @PropertyName(SELLER_MIN_SPEND_FIELD) val minSpend: Double? = null
)