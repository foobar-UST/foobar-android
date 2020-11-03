package com.foobarust.data.models

import com.foobarust.data.common.Constants.ITEMS_CHOICE_EXTRA_PRICE_FIELD
import com.foobarust.data.common.Constants.ITEMS_CHOICE_ID_FIELD
import com.foobarust.data.common.Constants.ITEMS_CHOICE_TITLE_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 10/12/20
 */

data class ItemChoiceEntity(
    @JvmField
    @PropertyName(ITEMS_CHOICE_ID_FIELD) val id: String? = null,

    @JvmField
    @PropertyName(ITEMS_CHOICE_TITLE_FIELD) val title: String? = null,

    @JvmField
    @PropertyName(ITEMS_CHOICE_EXTRA_PRICE_FIELD) val extraPrice: Double? = null
)