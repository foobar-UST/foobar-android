package com.foobarust.data.models

import com.foobarust.data.common.Constants.SELLER_ITEMS_CATALOG_ID_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_DESCRIPTION_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_ID_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_PRICE_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_TITLE_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 10/5/20
 *
 * Represent a document structure in 'items_basic' collection.
 */

data class ItemBasicEntity(

    @JvmField
    @PropertyName(SELLER_ITEMS_ID_FIELD) val id: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_TITLE_FIELD) val title: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_DESCRIPTION_FIELD) val description: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_CATALOG_ID_FIELD) val catalog_id: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_PRICE_FIELD) val price: Double? = null
)