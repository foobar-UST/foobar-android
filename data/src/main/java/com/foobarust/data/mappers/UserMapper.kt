package com.foobarust.data.mappers

import com.foobarust.data.models.user.UserCartItemEntity
import com.foobarust.data.models.user.UserDetailEntity
import com.foobarust.domain.models.user.UserCartItem
import com.foobarust.domain.models.user.UserDetail
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun toUserDetail(entity: UserDetailEntity): UserDetail {
        return UserDetail(
            username = entity.username!!,
            email = entity.email!!,
            name = entity.name,
            phoneNum = entity.phoneNum,
            photoUrl = entity.photoUrl,
            updatedAt = entity.updatedAt?.toDate(),
            dataCompleted = entity.dataCompleted ?: false
        )
    }

    fun toUserDetailEntity(userDetail: UserDetail): UserDetailEntity {
        return UserDetailEntity(
            username = userDetail.username,
            email = userDetail.email,
            name = userDetail.name,
            phoneNum = userDetail.phoneNum,
            photoUrl = userDetail.photoUrl,
            updatedAt = null
        )
    }

    fun toUserCartItem(entity: UserCartItemEntity): UserCartItem {
        return UserCartItem(
            id = entity.id!!,
            itemId = entity.itemId!!,
            itemTitle = entity.itemTitle!!,
            itemTitleZh = entity.itemTitleZh,
            itemPrice = entity.itemPrice!!,
            itemImageUrl = entity.itemImageUrl,
            amounts = entity.amounts!!,
            totalPrice = entity.totalPrice!!,
            notes = entity.notes,
            updatedAt = entity.updatedAt?.toDate()
        )
    }

    fun toUserCartItemEntity(userCartItem: UserCartItem): UserCartItemEntity {
        return UserCartItemEntity(
            id = userCartItem.id,
            itemId = userCartItem.itemId,
            itemTitle = userCartItem.itemTitle,
            itemTitleZh = userCartItem.itemTitleZh,
            itemPrice = userCartItem.itemPrice,
            itemImageUrl = userCartItem.itemImageUrl,
            totalPrice = userCartItem.totalPrice,
            notes = userCartItem.notes,
            amounts = userCartItem.amounts,
        )
    }
}