package com.foobarust.domain.serialize

import com.foobarust.domain.models.seller.SellerCatalog
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Created by kevin on 4/20/21
 */

@Serializable
internal data class SellerCatalogSerialized(
    val id: String,
    val seller_id: String,
    val title: String,
    val title_zh: String? = null,
    val available: Boolean
)

internal fun SellerCatalogSerialized.toSellerCatalog(): SellerCatalog {
    return SellerCatalog(
        id = id, title = title, titleZh = title_zh, available = available, updatedAt = Date()
    )
}