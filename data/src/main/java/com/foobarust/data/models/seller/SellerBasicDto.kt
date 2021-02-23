
package com.foobarust.data.models.seller

import com.foobarust.data.constants.Constants
import com.foobarust.data.constants.Constants.SELLER_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.SELLER_MIN_SPEND_FIELD
import com.foobarust.data.constants.Constants.SELLER_NAME_FIELD
import com.foobarust.data.constants.Constants.SELLER_NAME_ZH_FIELD
import com.foobarust.data.constants.Constants.SELLER_ONLINE_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_FIELD
import com.foobarust.data.constants.Constants.SELLER_TAGS_FIELD
import com.foobarust.data.constants.Constants.SELLER_TYPE_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 9/27/20
 *
 * Represent a document structure in 'sellers_basic' collection.
 */

data class SellerBasicDto(
    @JvmField
    @PropertyName(SELLER_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(SELLER_NAME_FIELD)
    val name: String? = null,

    @JvmField
    @PropertyName(SELLER_NAME_ZH_FIELD)
    val nameZh: String? = null,

    @JvmField
    @PropertyName(SELLER_IMAGE_URL_FIELD)
    val imageUrl: String? = null,

    @JvmField
    @PropertyName(SELLER_MIN_SPEND_FIELD)
    val minSpend: Double? = null,

    @JvmField
    @PropertyName(SELLER_RATING_FIELD)
    val rating: Double? = null,

    @JvmField
    @PropertyName(Constants.SELLER_RATING_COUNT_FIELD)
    val ratingCount: Int? = null,

    @JvmField
    @PropertyName(SELLER_TYPE_FIELD)
    val type: Int? = null,

    @JvmField
    @PropertyName(SELLER_ONLINE_FIELD)
    val online: Boolean? = null,

    @JvmField
    @PropertyName(SELLER_TAGS_FIELD)
    val tags: List<String>? = null
)