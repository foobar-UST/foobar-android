package com.foobarust.domain.models.seller

import java.util.*

/**
 * Created by kevin on 9/27/20
 */

data class SellerBasic(
    val id: String,
    val name: String,
    val nameZh: String?,
    val imageUrl: String?,
    val orderRating: Double,
    val type: SellerType,
    val online: Boolean,
    val minSpend: Double,
    val tags: List<String>
)

fun SellerBasic.getNormalizedName(): String {
    return if (nameZh != null) "$name $nameZh" else name
}

fun SellerBasic.getNormalizedOrderRating(): String {
    return if (orderRating == 0.0) "n.a." else String.format("%.1f", orderRating)
}

fun SellerBasic.getNormalizedMinSpendString(): String {
    return String.format("%.1f", minSpend)
}

fun SellerBasic.getNormalizedTags(): String {
    return tags.joinToString(separator = " · ") { it.capitalize(Locale.US) }
}