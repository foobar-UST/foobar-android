package com.foobarust.domain.usecases.cart

import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.models.seller.SellerDetail
import javax.inject.Inject

/**
 * Created by kevin on 2/21/21
 */

class CheckCartModifiableUseCase @Inject constructor() {

    operator fun invoke(
        userCart: UserCart,
        cartItems: List<UserCartItem>,
        sellerDetail: SellerDetail
    ): Boolean {
        // Decide whether the user can modify cart
        // 1. Cart is already synchronized and up-to-date
        // 2. All items in cart are available
        // 3. Seller is currently online
        return !userCart.syncRequired &&
            !cartItems.any { !it.available } &&
            cartItems.isNotEmpty() &&
            sellerDetail.online
    }
}