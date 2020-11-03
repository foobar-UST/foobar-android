package com.foobarust.data.models

import com.foobarust.data.common.Constants.SELLER_CATALOG_AVAILABLE_FIELD
import com.foobarust.data.common.Constants.SELLER_CATALOG_END_TIME_FIELD
import com.foobarust.data.common.Constants.SELLER_CATALOG_ID_FIELD
import com.foobarust.data.common.Constants.SELLER_CATALOG_NAME_FIELD
import com.foobarust.data.common.Constants.SELLER_CATALOG_START_TIME_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 10/10/20
 */

data class SellerCatalogEntity(
    @JvmField
    @PropertyName(SELLER_CATALOG_ID_FIELD) val id: String? = null,

    @JvmField
    @PropertyName(SELLER_CATALOG_NAME_FIELD) val name: String? = null,

    @JvmField
    @PropertyName(SELLER_CATALOG_AVAILABLE_FIELD) val available: Boolean? = null,

    // e.g. 09:30
    @JvmField
    @PropertyName(SELLER_CATALOG_START_TIME_FIELD) val startTime: String? = null,

    // e.g. 23:30
    @JvmField
    @PropertyName(SELLER_CATALOG_END_TIME_FIELD) val endTime: String? = null
)