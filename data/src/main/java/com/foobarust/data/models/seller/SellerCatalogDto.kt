package com.foobarust.data.models.seller

import com.foobarust.data.constants.Constants.SELLER_CATALOG_AVAILABLE_FIELD
import com.foobarust.data.constants.Constants.SELLER_CATALOG_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_CATALOG_TITLE_FIELD
import com.foobarust.data.constants.Constants.SELLER_CATALOG_TITLE_ZH_FIELD
import com.foobarust.data.constants.Constants.SELLER_CATALOG_UPDATED_AT_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

/**
 * Created by kevin on 10/10/20
 */

data class SellerCatalogDto(
    @JvmField
    @PropertyName(SELLER_CATALOG_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(SELLER_CATALOG_TITLE_FIELD)
    val title: String? = null,

    @JvmField
    @PropertyName(SELLER_CATALOG_TITLE_ZH_FIELD)
    val titleZh: String? = null,

    @JvmField
    @PropertyName(SELLER_CATALOG_AVAILABLE_FIELD)
    val available: Boolean? = null,

    @JvmField
    @ServerTimestamp
    @PropertyName(SELLER_CATALOG_UPDATED_AT_FIELD)
    val updatedAt: Timestamp? = null
)