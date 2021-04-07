package com.foobarust.data.mappers

import com.foobarust.data.models.user.UserDeliveryDto
import com.foobarust.data.models.user.UserDetailCacheDto
import com.foobarust.data.models.user.UserDetailNetworkDto
import com.foobarust.data.models.user.UserPublicDto
import com.foobarust.domain.models.user.UserDelivery
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.UserPublic
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun fromUserDetailNetworkDtoToUserDetail(dto: UserDetailNetworkDto): UserDetail {
        return UserDetail(
            id = dto.id!!,
            username = dto.username!!,
            email = dto.email!!,
            name = dto.name,
            phoneNum = dto.phoneNum,
            photoUrl = dto.photoUrl,
            updatedAt = dto.updatedAt?.toDate()
        )
    }

    fun fromUserDetailCacheDtoToUserDetail(dto: UserDetailCacheDto): UserDetail {
        return UserDetail(
            id = dto.id,
            username = dto.username!!,
            email = dto.email!!,
            name = dto.name,
            phoneNum = dto.phoneNum,
            photoUrl = dto.photoUrl,
            updatedAt = dto.updatedAt
        )
    }

    fun toUserDetailCacheDto(userDetail: UserDetail): UserDetailCacheDto {
        return UserDetailCacheDto(
            id = userDetail.id,
            name = userDetail.name,
            username = userDetail.username,
            email = userDetail.email,
            phoneNum = userDetail.phoneNum,
            photoUrl = userDetail.photoUrl,
            updatedAt = userDetail.updatedAt,
        )
    }

    fun toUserPublic(dto: UserPublicDto): UserPublic {
        return UserPublic(
            id = dto.id!!,
            username = dto.username!!,
            photoUrl = dto.photoUrl
        )
    }

    fun toUserDelivery(dto: UserDeliveryDto): UserDelivery {
        return UserDelivery(
            id = dto.id!!,
            name = dto.name,
            phoneNum = dto.phoneNum,
            photoUrl = dto.photoUrl
        )
    }
}