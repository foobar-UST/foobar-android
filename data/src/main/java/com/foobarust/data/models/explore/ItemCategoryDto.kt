package com.foobarust.data.models.explore

import com.foobarust.data.constants.Constants.ITEM_CATEGORY_ID_FIELD
import com.foobarust.data.constants.Constants.ITEM_CATEGORY_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.ITEM_CATEGORY_TAG_FIELD
import com.foobarust.data.constants.Constants.ITEM_CATEGORY_TITLE_FIELD
import com.foobarust.data.constants.Constants.ITEM_CATEGORY_TITLE_ZH_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 2/26/21
 */

data class ItemCategoryDto(
    @JvmField
    @PropertyName(ITEM_CATEGORY_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(ITEM_CATEGORY_TITLE_FIELD)
    val title: String? = null,

    @JvmField
    @PropertyName(ITEM_CATEGORY_TAG_FIELD)
    val tag: String? = null,

    @JvmField
    @PropertyName(ITEM_CATEGORY_TITLE_ZH_FIELD)
    val titleZh: String? = null,

    @JvmField
    @PropertyName(ITEM_CATEGORY_IMAGE_URL_FIELD)
    val imageUrl: String? = null
)