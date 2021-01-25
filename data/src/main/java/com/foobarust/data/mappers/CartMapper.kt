package com.foobarust.data.mappers

import com.foobarust.data.models.cart.AddUserCartItemRequest
import com.foobarust.data.models.cart.UpdateUserCartItemRequest
import com.foobarust.data.models.cart.UserCartDto
import com.foobarust.data.models.cart.UserCartItemDto
import com.foobarust.domain.models.cart.AddUserCartItem
import com.foobarust.domain.models.cart.UpdateUserCartItem
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.models.seller.SellerType
import javax.inject.Inject

class CartMapper @Inject constructor() {

    fun toUserCart(dto: UserCartDto): UserCart {
        return UserCart(
            userId = dto.userId!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            sellerId = dto.sellerId!!,
            sellerType = SellerType.values()[dto.sellerType!!],
            sectionId = dto.sectionId,
            deliveryTime = dto.deliveryTime?.toDate(),
            imageUrl = dto.imageUrl,
            pickupLocation = dto.pickupLocation!!.toGeolocation(),
            itemsCount = dto.itemsCount ?: 0,
            subtotalCost = dto.subtotalCost ?: 0.0,
            deliveryCost = dto.deliveryCost ?: 0.0,
            totalCost = dto.totalCost ?: 0.0,
            syncRequired = dto.syncRequired ?: false,
            updatedAt = dto.updatedAt?.toDate()
        )
    }

    fun toUserCartItem(dto: UserCartItemDto): UserCartItem {
        return UserCartItem(
            id = dto.id!!,
            itemId = dto.itemId!!,
            itemSellerId = dto.itemSellerId!!,
            itemSectionId = dto.itemSectionId,
            itemTitle = dto.itemTitle!!,
            itemTitleZh = dto.itemTitleZh,
            itemPrice = dto.itemPrice!!,
            itemImageUrl = dto.itemImageUrl,
            amounts = dto.amounts!!,
            totalPrice = dto.totalPrice!!,
            available = dto.available ?: true,
            updatedAt = dto.updatedAt?.toDate()
        )
    }

    fun toAddUserCartItemRequest(addUserCartItem: AddUserCartItem): AddUserCartItemRequest {
        return AddUserCartItemRequest(
            itemId = addUserCartItem.itemId,
            amounts = addUserCartItem.amounts,
            sectionId = addUserCartItem.sectionId
        )
    }

    fun toUpdateUserCartItemRequest(updateUserCartItem: UpdateUserCartItem): UpdateUserCartItemRequest {
        return UpdateUserCartItemRequest(
            cartItemId = updateUserCartItem.cartItemId,
            amounts = updateUserCartItem.amounts
        )
    }
}