package com.foobarust.data.models.seller

import com.foobarust.data.constants.Constants.SELLER_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_IMAGE_URL_FIELD
import com.foobarust.data.constants.Constants.SELLER_MIN_SPEND_FIELD
import com.foobarust.data.constants.Constants.SELLER_NAME_FIELD
import com.foobarust.data.constants.Constants.SELLER_NAME_ZH_FIELD
import com.foobarust.data.constants.Constants.SELLER_ONLINE_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_COUNT_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_FIELD
import com.foobarust.data.constants.Constants.SELLER_TAGS_FIELD
import com.foobarust.data.constants.Constants.SELLER_TYPE_FIELD
import com.google.gson.annotations.SerializedName

/**
 * Created by kevin on 2/23/21
 */

data class SearchSellerResponse(
    @SerializedName(SELLER_ID_FIELD)
    val id: String,

    @SerializedName(SELLER_NAME_FIELD)
    val name: String,

    @SerializedName(SELLER_NAME_ZH_FIELD)
    val nameZh: String?,

    @SerializedName(SELLER_IMAGE_URL_FIELD)
    val imageUrl: String?,

    @SerializedName(SELLER_MIN_SPEND_FIELD)
    val minSpend: Double,

    @SerializedName(SELLER_RATING_FIELD)
    val rating: Double,

    @SerializedName(SELLER_RATING_COUNT_FIELD)
    val ratingCount: Int,

    @SerializedName(SELLER_TYPE_FIELD)
    val type: Int,

    @SerializedName(SELLER_ONLINE_FIELD)
    val online: Boolean,

    @SerializedName(SELLER_TAGS_FIELD)
    val tags: List<String>
)