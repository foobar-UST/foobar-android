package com.foobarust.domain.serialize

import com.foobarust.domain.models.seller.SellerRatingBasic
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Created by kevin on 4/20/21
 */

@Serializable
internal data class SellerRatingSerialized(
    val id: String,
    val username: String,
    val user_photo_url: String,
    val seller_id: String,
    val order_id: String,
    val order_rating: Double,
    val delivery_rating: Boolean,
    val comment: String? = null
)

internal fun SellerRatingSerialized.toSellerRatingBasic(): SellerRatingBasic {
    return SellerRatingBasic(
        id = id, username = username, userPhotoUrl = user_photo_url, orderRating = order_rating,
        deliveryRating = delivery_rating, createdAt = Date(), comment = comment
    )
}