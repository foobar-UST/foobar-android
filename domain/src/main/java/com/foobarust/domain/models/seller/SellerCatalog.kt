package com.foobarust.domain.models.seller

import java.util.*

/**
 * Created by kevin on 10/4/20
 */

data class SellerCatalog(
    val id: String,
    val title: String,
    val titleZh: String?,
    val available: Boolean,
    val updatedAt: Date?
)

fun SellerCatalog.getNormalizedTitle(): String {
    return if (titleZh != null) "$title $titleZh" else title
}