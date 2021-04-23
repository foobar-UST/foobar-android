package com.foobarust.domain.serialize

import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.models.seller.SellerDetail
import com.foobarust.domain.models.seller.SellerType
import kotlinx.serialization.Serializable

/**
 * Created by kevin on 4/19/21
 */

@Serializable
internal data class SellerSerialized(
    val id: String,
    val name: String,
    val name_zh: String? = null,
    val description: String? = null,
    val description_zh: String? = null,
    val website: String? = null,
    val phone_num: String,
    val location: GeolocationSerialized,
    val image_url: String? = null,
    val min_spend: Double,
    val order_rating: Double,
    val delivery_rating: Double? = null,
    val rating_count: SellerRatingCountSerialized,
    val type: Int,
    val online: Boolean,
    val notice: String? = null,
    val opening_hours: String,
    val tags: List<String>,
    val deliveryCost: Double? = null
)

internal fun SellerSerialized.toSellerDetail(): SellerDetail {
    return SellerDetail(
        id = id, name = name, nameZh = name_zh, description = description,
        descriptionZh = description_zh, phoneNum = phone_num, website = website,
        location = location.toGeolocation(),
        imageUrl = image_url, minSpend = min_spend, orderRating = order_rating,
        deliveryRating = delivery_rating,
        ratingCount = rating_count.toSellerRatingCount(),
        type = SellerType.values()[type], online = online, notice = notice,
        openingHours = opening_hours, tags = tags
    )
}

internal fun SellerSerialized.toSellerBasic(): SellerBasic {
    return SellerBasic(
        id = id, name = name, nameZh = name_zh, imageUrl = image_url,
        minSpend = min_spend, orderRating = order_rating,
        type = SellerType.values()[type], online = online, tags = tags
    )
}