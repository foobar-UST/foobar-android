package com.foobarust.data.models.promotion

import com.foobarust.data.common.Constants.SUGGESTS_BASIC_ID_FIELD
import com.foobarust.data.common.Constants.SUGGESTS_BASIC_IMAGE_URL_FIELD
import com.foobarust.data.common.Constants.SUGGESTS_BASIC_ITEM_ID_FIELD
import com.foobarust.data.common.Constants.SUGGESTS_BASIC_ITEM_TITLE_FIELD
import com.foobarust.data.common.Constants.SUGGESTS_BASIC_SELLER_NAME_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 10/3/20
 *
 * Represent a document structure in '/users/suggests_basic' sub-collection.
 */

// TODO: refactor suggest basic
data class SuggestBasicEntity(
    @JvmField
    @PropertyName(SUGGESTS_BASIC_ID_FIELD) val id: String? = null,

    @JvmField
    @PropertyName(SUGGESTS_BASIC_ITEM_ID_FIELD) val itemId: String? = null,

    @JvmField
    @PropertyName(SUGGESTS_BASIC_ITEM_TITLE_FIELD) val itemTitle: String? = null,

    @JvmField
    @PropertyName(SUGGESTS_BASIC_SELLER_NAME_FIELD) val sellerName: String? = null,

    @JvmField
    @PropertyName(SUGGESTS_BASIC_IMAGE_URL_FIELD) val imageUrl: String? = null
)