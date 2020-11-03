package com.foobarust.data.models

import com.foobarust.data.common.Constants.EXTRA_ITEM_ID_FIELD
import com.foobarust.data.common.Constants.EXTRA_ITEM_PRICE_FIELD
import com.foobarust.data.common.Constants.EXTRA_ITEM_TITLE_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 10/13/20
 */

data class ExtraItemEntity(
    @JvmField
    @PropertyName(EXTRA_ITEM_ID_FIELD) val id: String? = null,

    @JvmField
    @PropertyName(EXTRA_ITEM_TITLE_FIELD) val title: String? = null,

    @JvmField
    @PropertyName(EXTRA_ITEM_PRICE_FIELD) val price: Double? = null
)