package com.foobarust.data.mappers

import com.foobarust.data.models.cart.UserCartEntity
import com.foobarust.data.models.cart.UserCartItemEntity
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.models.seller.SellerType
import javax.inject.Inject

class CartMapper @Inject constructor() {

    fun toUserCart(entity: UserCartEntity): UserCart {
        return UserCart(
            sellerId = entity.sellerId,
            sellerType = entity.sellerType?.let {SellerType.values()[it] },
            itemsCount = entity.itemsCount,
            subtotalCost = entity.subtotalCost ?: 0.0,
            deliveryCost = entity.deliveryCost ?: 0.0,
            totalCost = entity.totalCost ?: 0.0,
            syncRequired = entity.syncRequired ?: false,
            updatedAt = entity.updatedAt?.toDate()
        )
    }

    fun toUserCartItem(entity: UserCartItemEntity): UserCartItem {
        return UserCartItem(
            id = entity.id!!,
            itemId = entity.itemId!!,
            itemSellerId = entity.itemSellerId!!,
            itemTitle = entity.itemTitle!!,
            itemTitleZh = entity.itemTitleZh,
            itemPrice = entity.itemPrice!!,
            itemImageUrl = entity.itemImageUrl,
            amounts = entity.amounts!!,
            totalPrice = entity.totalPrice!!,
            available = entity.available ?: true,
            updatedAt = entity.updatedAt?.toDate()
        )
    }
}