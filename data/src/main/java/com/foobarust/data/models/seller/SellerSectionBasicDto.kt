package com.foobarust.data.models.seller

import com.foobarust.data.constants.Constants.SELLER_SECTION_AVAILABLE_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_CUTOFF_TIME_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_DELIVERY_TIME_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_JOINED_USERS_COUNT_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_MAX_USERS_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_SELLER_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_SELLER_NAME_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_SELLER_NAME_ZH_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_STATE_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_TITLE_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_TITLE_ZH_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 12/20/20
 */

data class SellerSectionBasicDto(
    @JvmField
    @PropertyName(SELLER_SECTION_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_TITLE_FIELD)
    val title: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_TITLE_ZH_FIELD)
    val titleZh: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_SELLER_ID_FIELD)
    val sellerId: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_SELLER_NAME_FIELD)
    val sellerName: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_SELLER_NAME_ZH_FIELD)
    val sellerNameZh: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_DELIVERY_TIME_FIELD)
    val deliveryTime: Timestamp? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_CUTOFF_TIME_FIELD)
    val cutoffTime: Timestamp? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_MAX_USERS_FIELD)
    val maxUsers: Int? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_JOINED_USERS_COUNT_FIELD)
    val joinedUsersCount: Int? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_IMAGE_URL_FIELD)
    val imageUrl: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_STATE_FIELD)
    val state: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_AVAILABLE_FIELD)
    val available: Boolean? = null
)