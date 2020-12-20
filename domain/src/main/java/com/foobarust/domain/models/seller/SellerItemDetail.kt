package com.foobarust.domain.models.seller

import java.util.*

/**
 * Created by kevin on 10/5/20
 */

data class SellerItemDetail(
    val id: String,
    val title: String,
    val titleZh: String?,
    val description: String?,
    val descriptionZh: String?,
    val price: Double,
    val imageUrl: String?,
    val count: Int,
    val available: Boolean,
    val updatedAt: Date?
)

fun SellerItemDetail.getNormalizedTitle(): String {
    return if (titleZh != null) "$title\n$titleZh" else title
}

fun SellerItemDetail.getNormalizedDescription(): String? {
    return if (descriptionZh != null) "$description\n$descriptionZh" else description
}