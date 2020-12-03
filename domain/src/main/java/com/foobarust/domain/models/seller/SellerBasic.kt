package com.foobarust.domain.models.seller

/**
 * Created by kevin on 9/27/20
 */

data class SellerBasic(
    val id: String,
    val name: String,
    val nameZh: String?,
    val imageUrl: String?,
    val rating: Double,
    val type: SellerType,
    val online: Boolean,
    val minSpend: Double,
    val tags: List<String>
)

fun SellerBasic.getNormalizedName(): String {
    return if (nameZh != null) "$name $nameZh" else name
}