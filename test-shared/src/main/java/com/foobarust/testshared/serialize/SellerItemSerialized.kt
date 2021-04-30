package com.foobarust.testshared.serialize

import com.foobarust.domain.models.seller.SellerItemBasic
import com.foobarust.domain.models.seller.SellerItemDetail
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Created by kevin on 4/20/21
 */

@Serializable
data class SellerItemSerialized(
    val id: String,
    val title: String,
    val title_zh: String? = null,
    val description: String? = null,
    val description_zh: String? = null,
    val catalog_id: String,
    val seller_id: String,
    val price: Double,
    val image_url: String? = null,
    val count: Int,
    val available: Boolean
)

fun SellerItemSerialized.toSellerItemDetail(): SellerItemDetail {
    return SellerItemDetail(
        id = id, title = title, titleZh = title_zh, description = description,
        descriptionZh = description_zh, price = price, imageUrl = image_url, count = count,
        available = available, updatedAt = Date()
    )
}

fun SellerItemSerialized.toSellerItemBasic(): SellerItemBasic {
    return SellerItemBasic(
        id = id, title = title, titleZh = title_zh, price = price, imageUrl = image_url,
        count = count, available = available
    )
}