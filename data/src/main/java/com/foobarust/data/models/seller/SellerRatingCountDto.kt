package com.foobarust.data.models.seller

import com.foobarust.data.constants.Constants.SELLER_RATING_COUNT_EXCELLENT_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_COUNT_FAIR_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_COUNT_GOOD_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_COUNT_POOR_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_COUNT_VERY_GOOD_FIELD
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 3/3/21
 */

data class SellerRatingCountDto(
    @JvmField
    @PropertyName(SELLER_RATING_COUNT_EXCELLENT_FIELD)
    val excellent: Int? = null,

    @JvmField
    @PropertyName(SELLER_RATING_COUNT_VERY_GOOD_FIELD)
    val veryGood: Int? = null,

    @JvmField
    @PropertyName(SELLER_RATING_COUNT_GOOD_FIELD)
    val good: Int? = null,

    @JvmField
    @PropertyName(SELLER_RATING_COUNT_FAIR_FIELD)
    val fair: Int? = null,

    @JvmField
    @PropertyName(SELLER_RATING_COUNT_POOR_FIELD)
    val poor: Int? = null
)