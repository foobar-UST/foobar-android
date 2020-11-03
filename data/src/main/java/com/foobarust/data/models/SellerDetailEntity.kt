package com.foobarust.data.models

import com.foobarust.data.common.Constants.SELLER_CATALOGS_FIELD
import com.foobarust.data.common.Constants.SELLER_DESCRIPTION_FIELD
import com.foobarust.data.common.Constants.SELLER_EMAIL_FIELD
import com.foobarust.data.common.Constants.SELLER_ID_FIELD
import com.foobarust.data.common.Constants.SELLER_IMGAE_URL_FIELD
import com.foobarust.data.common.Constants.SELLER_LOCATION_FIELD
import com.foobarust.data.common.Constants.SELLER_MIN_SPEND_FIELD
import com.foobarust.data.common.Constants.SELLER_NAME_FIELD
import com.foobarust.data.common.Constants.SELLER_NOTICE_FIELD
import com.foobarust.data.common.Constants.SELLER_ONLINE_FIELD
import com.foobarust.data.common.Constants.SELLER_OPENING_HOURS_FIELD
import com.foobarust.data.common.Constants.SELLER_PHONE_NUM_FIELD
import com.foobarust.data.common.Constants.SELLER_RATING_FIELD
import com.foobarust.data.common.Constants.SELLER_TYPE_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 9/27/20
 *
 * Represent a document structure in 'sellers' collection.
 */

data class SellerDetailEntity(
    @JvmField
    @PropertyName(SELLER_ID_FIELD) val id: String? = null,

    @JvmField
    @PropertyName(SELLER_NAME_FIELD) val name: String? = null,

    @JvmField
    @PropertyName(SELLER_DESCRIPTION_FIELD) val description: String? = null,

    @JvmField
    @PropertyName(SELLER_EMAIL_FIELD) val email: String? = null,

    @JvmField
    @PropertyName(SELLER_PHONE_NUM_FIELD) val phone_num: String? = null,

    @JvmField
    @PropertyName(SELLER_LOCATION_FIELD) val location: SellerLocationEntity? = null,

    @JvmField
    @PropertyName(SELLER_IMGAE_URL_FIELD) val image_url: String? = null,

    @JvmField
    @PropertyName(SELLER_MIN_SPEND_FIELD) val min_spend: Double? = null,

    @JvmField
    @PropertyName(SELLER_RATING_FIELD) val rating: Double? = null,

    @JvmField
    @PropertyName(SELLER_CATALOGS_FIELD) val catalogs: List<SellerCatalogEntity>? = null,

    @JvmField
    @PropertyName(SELLER_TYPE_FIELD) val type: Int? = null,

    @JvmField
    @PropertyName(SELLER_ONLINE_FIELD) val online: Boolean? = null,

    @JvmField
    @PropertyName(SELLER_OPENING_HOURS_FIELD) val openingHours: String? = null,

    @JvmField
    @PropertyName(SELLER_NOTICE_FIELD) val notice: String? = null
)