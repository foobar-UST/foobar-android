package com.foobarust.domain.models.seller

/**
 * Created by kevin on 10/5/20
 */

data class SellerItemBasic(
    val id: String,
    val title: String,
    val titleZh: String?,
    val price: Double,
    val imageUrl: String?,
    val count: Int,
    val available: Boolean
)

fun SellerItemBasic.getNormalizedTitle(): String {
    return if (titleZh != null) "$title\n$titleZh" else title
}

fun SellerItemBasic.purchasable(): Boolean {
    return available && count > 0
}