package com.foobarust.domain.models

/**
 * Created by kevin on 10/5/20
 */

data class SellerItemDetail(
    val id: String,
    val title: String,
    val description: String,
    val sellerId: String,
    val catalogId: String,
    val price: Double,
    val imageUrl: String? = null,
    val choices: List<SellerItemChoice>,
    val extraItems: List<SellerItemExtraItem>,
    val available: Boolean
)