package com.foobarust.data.models.seller

import com.foobarust.data.constants.Constants.SELLER_SECTION_AVAILABLE_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_CUTOFF_TIME_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_DELIVERER_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_DELIVERER_LOCATION_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_DELIVERER_TRAVEL_MODE_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_DELIVERY_COST_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_DELIVERY_LOCATION_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_DELIVERY_TIME_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_DESCRIPTION_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_DESCRIPTION_ZH_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_GROUP_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_JOINED_USERS_COUNT_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_JOINED_USERS_IDS_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_MAX_USERS_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_SELLER_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_SELLER_NAME_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_SELLER_NAME_ZH_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_STATE_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_TITLE_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_TITLE_ZH_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_UPDATED_AT_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

/**
 * Created by kevin on 12/20/20
 */

data class SellerSectionDetailDto(
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
    @PropertyName(SELLER_SECTION_GROUP_ID_FIELD)
    val groupId: String? = null,

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
    @PropertyName(SELLER_SECTION_DELIVERER_ID_FIELD)
    val delivererId: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_DELIVERER_LOCATION_FIELD)
    val delivererLocation: GeoPoint? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_DELIVERER_TRAVEL_MODE_FIELD)
    val delivererTravelMode: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_DELIVERY_COST_FIELD)
    val deliveryCost: Double? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_DELIVERY_TIME_FIELD)
    val deliveryTime: Timestamp? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_DELIVERY_LOCATION_FIELD)
    val deliveryLocation: GeolocationDto? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_CUTOFF_TIME_FIELD)
    val cutoffTime: Timestamp? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_DESCRIPTION_FIELD)
    val description: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_DESCRIPTION_ZH_FIELD)
    val descriptionZh: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_MAX_USERS_FIELD)
    val maxUsers: Int? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_JOINED_USERS_COUNT_FIELD)
    val joinedUsersCount: Int? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_JOINED_USERS_IDS_FIELD)
    val joinedUsersIds: List<String>? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_IMAGE_URL_FIELD)
    val imageUrl: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_STATE_FIELD)
    val state: String? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_AVAILABLE_FIELD)
    val available: Boolean? = null,

    @JvmField
    @PropertyName(SELLER_SECTION_UPDATED_AT_FIELD)
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)