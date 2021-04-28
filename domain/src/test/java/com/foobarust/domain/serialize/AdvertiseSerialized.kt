package com.foobarust.domain.serialize

import com.foobarust.domain.models.promotion.AdvertiseBasic
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Created by kevin on 4/28/21
 */

@Serializable
internal data class AdvertiseSerialized(
    val id: String,
    val seller_id: String,
    val title: String,
    val title_zh: String? = null,
    val content: String,
    val content_zh: String? = null,
    val image_url: String? = null,
    val seller_type: Int,
    val url: String
)

internal fun AdvertiseSerialized.toAdvertiseBasic(): AdvertiseBasic {
    return AdvertiseBasic(
        id = id, url = url, imageUrl = image_url, createdAt = Date()
    )
}

