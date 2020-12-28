package com.foobarust.data.mappers

import com.foobarust.data.models.user.UserDetailEntity
import com.foobarust.data.models.user.UserPublicEntity
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.UserPublic
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun toUserDetail(entity: UserDetailEntity): UserDetail {
        return UserDetail(
            id = entity.id!!,
            username = entity.username!!,
            email = entity.email!!,
            name = entity.name,
            phoneNum = entity.phoneNum,
            photoUrl = entity.photoUrl,
            updatedAt = entity.updatedAt?.toDate()
        )
    }

    fun toUserDetailEntity(userDetail: UserDetail): UserDetailEntity {
        return UserDetailEntity(
            id = userDetail.id,
            username = userDetail.username,
            email = userDetail.email,
            name = userDetail.name,
            phoneNum = userDetail.phoneNum,
            photoUrl = userDetail.photoUrl,
            updatedAt = null
        )
    }

    fun toUserPublic(entity: UserPublicEntity): UserPublic {
        return UserPublic(
            id = entity.id!!,
            username = entity.username!!,
            photoUrl = entity.photoUrl
        )
    }
}