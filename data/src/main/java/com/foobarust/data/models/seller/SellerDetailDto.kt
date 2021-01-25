package com.foobarust.data.models.seller

import com.foobarust.data.common.Constants.SELLER_BY_USER_ID
import com.foobarust.data.common.Constants.SELLER_DESCRIPTION_FIELD
import com.foobarust.data.common.Constants.SELLER_DESCRIPTION_ZH_FIELD
import com.foobarust.data.common.Constants.SELLER_ID_FIELD
import com.foobarust.data.common.Constants.SELLER_IMAGE_URL_FIELD
import com.foobarust.data.common.Constants.SELLER_LOCATION_FIELD
import com.foobarust.data.common.Constants.SELLER_MIN_SPEND_FIELD
import com.foobarust.data.common.Constants.SELLER_NAME_FIELD
import com.foobarust.data.common.Constants.SELLER_NAME_ZH_FIELD
import com.foobarust.data.common.Constants.SELLER_NOTICE_FIELD
import com.foobarust.data.common.Constants.SELLER_ONLINE_FIELD
import com.foobarust.data.common.Constants.SELLER_OPENING_HOURS_FIELD
import com.foobarust.data.common.Constants.SELLER_PHONE_NUM_FIELD
import com.foobarust.data.common.Constants.SELLER_RATING_COUNT_FIELD
import com.foobarust.data.common.Constants.SELLER_RATING_FIELD
import com.foobarust.data.common.Constants.SELLER_TAGS_FIELD
import com.foobarust.data.common.Constants.SELLER_TYPE_FIELD
import com.foobarust.data.common.Constants.SELLER_WEBSITE_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 9/27/20
 *
 * Represent a document structure in 'sellers' collection.
 */

data class SellerDetailDto(
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
    @PropertyName(SELLER_DESCRIPTION_FIELD)
    val description: String? = null,

    @JvmField
    @PropertyName(SELLER_DESCRIPTION_ZH_FIELD)
    val descriptionZh: String? = null,

    @JvmField
    @PropertyName(SELLER_WEBSITE_FIELD)
    val website: String? = null,

    @JvmField
    @PropertyName(SELLER_PHONE_NUM_FIELD)
    val phone_num: String? = null,

    @JvmField
    @PropertyName(SELLER_LOCATION_FIELD)
    val location: GeolocationDto? = null,

    @JvmField
    @PropertyName(SELLER_IMAGE_URL_FIELD)
    val image_url: String? = null,

    @JvmField
    @PropertyName(SELLER_MIN_SPEND_FIELD)
    val min_spend: Double? = null,

    @JvmField
    @PropertyName(SELLER_RATING_FIELD)
    val rating: Double? = null,

    @JvmField
    @PropertyName(SELLER_RATING_COUNT_FIELD)
    val ratingCount: Int? = null,

    @JvmField
    @PropertyName(SELLER_TYPE_FIELD)
    val type: Int? = null,

    @JvmField
    @PropertyName(SELLER_ONLINE_FIELD)
    val online: Boolean? = null,

    @JvmField
    @PropertyName(SELLER_NOTICE_FIELD)
    val notice: String? = null,

    @JvmField
    @PropertyName(SELLER_OPENING_HOURS_FIELD)
    val openingHours: String? = null,

    @JvmField
    @PropertyName(SELLER_TAGS_FIELD)
    val tags: List<String>? = null,

    @JvmField
    @PropertyName(SELLER_BY_USER_ID)
    val byUserId: String? = null,
)