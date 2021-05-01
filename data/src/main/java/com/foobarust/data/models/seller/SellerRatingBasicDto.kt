package com.foobarust.data.models.seller

import com.foobarust.data.constants.Constants.SELLER_RATING_CREATED_AT_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_DELIVERY_RATING_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_ORDER_RATING_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_USERNAME_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_USER_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_RATING_USER_PHOTO_URL_FIELD
import com.foobarust.data.constants.Constants.SUBMIT_ORDER_RATING_COMMENT
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * Created by kevin on 3/3/21
 */

data class SellerRatingBasicDto(
    @JvmField
    @PropertyName(SELLER_RATING_ID_FIELD)
    val id: String? = null,

    @JvmField
    @PropertyName(SELLER_RATING_USER_ID_FIELD)
    val userId: String? = null,

    @JvmField
    @PropertyName(SELLER_RATING_USERNAME_FIELD)
    val username: String? = null,

    @JvmField
    @PropertyName(SELLER_RATING_USER_PHOTO_URL_FIELD)
    val userPhotoUrl: String? = null,

    @JvmField
    @PropertyName(SELLER_RATING_ORDER_RATING_FIELD)
    val orderRating: Double? = null,

    @JvmField
    @PropertyName(SELLER_RATING_DELIVERY_RATING_FIELD)
    val deliveryRating: Boolean? = null,

    @JvmField
    @PropertyName(SUBMIT_ORDER_RATING_COMMENT)
    val comment: String? = null,

    @JvmField
    @PropertyName(SELLER_RATING_CREATED_AT_FIELD)
    val createdAt: Timestamp? = null
)