package com.foobarust.data.mappers

import com.foobarust.data.models.user.UserDetailCacheDto
import com.foobarust.data.models.user.UserDetailNetworkDto
import com.foobarust.data.models.user.UserPublicDto
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.UserPublic
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun fromNetworkDtoToUserDetail(networkDto: UserDetailNetworkDto): UserDetail {
        return UserDetail(
            id = networkDto.id!!,
            username = networkDto.username!!,
            email = networkDto.email!!,
            name = networkDto.name,
            phoneNum = networkDto.phoneNum,
            photoUrl = networkDto.photoUrl,
            updatedAt = networkDto.updatedAt?.toDate()
        )
    }

    fun fromCacheDtoToUserDetail(cacheDto: UserDetailCacheDto): UserDetail {
        return UserDetail(
            id = cacheDto.id,
            username = cacheDto.username!!,
            email = cacheDto.email!!,
            name = cacheDto.name,
            phoneNum = cacheDto.phoneNum,
            photoUrl = cacheDto.photoUrl,
            updatedAt = cacheDto.updatedAt
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
}