package com.foobarust.domain.models.cart

import com.foobarust.domain.models.seller.SellerType
import java.util.*

data class UserCart(
    val sellerId: String?,
    val sellerType: SellerType?,
    val itemsCount: Int,
    val subtotalCost: Double,
    val deliveryCost: Double,
    val totalCost: Double,
    val syncRequired: Boolean,
    val updatedAt: Date?
)