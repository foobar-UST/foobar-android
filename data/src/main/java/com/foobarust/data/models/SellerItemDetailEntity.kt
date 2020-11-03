package com.foobarust.data.models

import com.foobarust.data.common.Constants.SELLER_ITEMS_AVAILABLE_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_CATALOG_ID_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_CHOICES_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_COUNT_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_DESCRIPTION_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_EXTRA_ITEMS_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_ID_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_IMAGE_URL_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_PRICE_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_SELLER_ID_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_TITLE_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 10/5/20
 *
 * Represent a document structure in 'items' collection.
 */

data class SellerItemDetailEntity(
    @JvmField
    @PropertyName(SELLER_ITEMS_ID_FIELD) val id: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_TITLE_FIELD) val title: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_DESCRIPTION_FIELD) val description: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_SELLER_ID_FIELD) val sellerId: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_CATALOG_ID_FIELD) val catalogId: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_PRICE_FIELD) val price: Double? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_COUNT_FIELD) val count: Int? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_IMAGE_URL_FIELD) val imageUrl: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_CHOICES_FIELD) val choices: List<ItemChoiceEntity>? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_EXTRA_ITEMS_FIELD) val extraItems: List<ExtraItemEntity>? = null,

    @JvmField
    @PropertyName(SELLER_ITEMS_AVAILABLE_FIELD) val available: Boolean? = null
)