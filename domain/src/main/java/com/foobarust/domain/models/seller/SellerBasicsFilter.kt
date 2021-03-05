package com.foobarust.domain.models.seller

/**
 * Created by kevin on 2/27/21
 */

data class SellerBasicsFilter(
    val sellerType: SellerType? = null,     // Null for both types
    val categoryTag: String? = null,        // Null for all categories
)