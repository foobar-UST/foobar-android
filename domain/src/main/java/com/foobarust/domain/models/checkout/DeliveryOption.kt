package com.foobarust.domain.models.checkout

import com.foobarust.domain.models.seller.SellerType

/**
 * Created by kevin on 1/10/21
 */

data class DeliveryOption(
    val id: String,
    val identifier: String,
    val forSellerType: SellerType
)