package com.foobarust.data.models.seller

import com.foobarust.data.constants.Constants.SELLER_ITEM_AVAILABLE_FIELD
import com.foobarust.data.constants.Constants.SELLER_ITEM_CATALOG_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_ITEM_COUNT_FIELD
import com.foobarust.data.constants.Constants.SELLER_ITEM_DESCRIPTION_FIELD
import com.foobarust.data.constants.Constants.SELLER_ITEM_DESCRIPTION_ZH_FIELD
import com.foobarust.data.constants.Constants.SELLER_ITEM_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_ITEM_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.SELLER_ITEM_PRICE_FIELD
import com.foobarust.data.constants.Constants.SELLER_ITEM_SELLER_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_ITEM_TITLE_FIELD
import com.foobarust.data.constants.Constants.SELLER_ITEM_TITLE_ZH_FIELD
import com.foobarust.data.constants.Constants.SELLER_ITEM_UPDATED_AT_FIELD
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

/**
 * Created by kevin on 10/5/20
 */

data class SellerItemDetailDto(
    @JvmField
    @PropertyName(SELLER_ITEM_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEM_TITLE_FIELD)
    val title: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEM_TITLE_ZH_FIELD)
    val titleZh: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEM_DESCRIPTION_FIELD)
    val description: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEM_DESCRIPTION_ZH_FIELD)
    val descriptionZh: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEM_CATALOG_ID_FIELD)
    val catalogId: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEM_SELLER_ID_FIELD)
    val sellerId: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEM_PRICE_FIELD)
    val price: Double? = null,

    @JvmField
    @PropertyName(SELLER_ITEM_IMAGE_URL_FIELD)
    val imageUrl: String? = null,

    @JvmField
    @PropertyName(SELLER_ITEM_COUNT_FIELD)
    val count: Int? = null,

    @JvmField
    @PropertyName(SELLER_ITEM_AVAILABLE_FIELD)
    val available: Boolean? = null,

    @JvmField
    @ServerTimestamp
    @PropertyName(SELLER_ITEM_UPDATED_AT_FIELD)
    val updatedAt: Timestamp? = null
)