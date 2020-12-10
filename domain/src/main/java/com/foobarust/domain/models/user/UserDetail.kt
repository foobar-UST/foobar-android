package com.foobarust.domain.models.user

import com.foobarust.domain.models.seller.SellerType
import java.util.*


/**
 * Created by kevin on 9/12/20
 */

// TODO: fix nullable fields
data class UserDetail(
    val name: String? = null,
    val username: String,
    val email: String,
    val phoneNum: String? = null,
    val photoUrl: String? = null,
    val cartSellerId: String? = null,
    val cartSellerType: SellerType? = null,
    val cartItemsCount: Int? = null,
    val cartSubtotalCost: Double? = null,
    val cartDeliveryCost: Double? = null,
    val cartTotalCost: Double? = null,
    val cartUpdatedAt: Date? = null,
    val updatedAt: Date?
)

fun UserDetail.isDataCompletedForOrdering(): Boolean {
    return !name.isNullOrEmpty() && !phoneNum.isNullOrEmpty()
}
