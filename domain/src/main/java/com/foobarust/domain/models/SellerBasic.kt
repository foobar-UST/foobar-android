package com.foobarust.domain.models

/**
 * Created by kevin on 9/27/20
 */

data class SellerBasic(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val rating: Double,
    val type: SellerType,
    val online: Boolean,
    val minSpend: Double
)
