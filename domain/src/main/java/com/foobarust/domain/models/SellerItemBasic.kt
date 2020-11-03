package com.foobarust.domain.models

/**
 * Created by kevin on 10/5/20
 */

data class SellerItemBasic(
    val id: String,
    val title: String,
    val description: String,
    val catalogId: String,
    val price: Double,
    val available: Boolean
)