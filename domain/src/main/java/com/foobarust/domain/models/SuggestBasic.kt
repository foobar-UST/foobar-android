package com.foobarust.domain.models

/**
 * Created by kevin on 10/3/20
 */

data class SuggestBasic(
    val id: String,
    val itemId: String,
    val itemTitle: String,
    val sellerName: String,
    val imageUrl: String? = null
)