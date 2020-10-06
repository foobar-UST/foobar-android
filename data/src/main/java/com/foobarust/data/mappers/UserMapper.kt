package com.foobarust.data.mappers

import com.foobarust.data.common.Constants.USER_NAME_FIELD
import com.foobarust.data.common.Constants.USER_PHONE_NUM_FIELD
import com.foobarust.data.models.UserDetailEntity
import com.foobarust.domain.models.UserDetail
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun toUserDetail(userDetailEntity: UserDetailEntity): UserDetail {
        return UserDetail(
            username = userDetailEntity.username!!,
            email = userDetailEntity.email!!,
            name = userDetailEntity.name,
            phoneNum = userDetailEntity.phoneNum,
            photoUrl = userDetailEntity.photoUrl,
            allowOrder = userDetailEntity.allowOrder ?: false
        )
    }

    fun toUserDetailEntity(userDetail: UserDetail): UserDetailEntity {
        return UserDetailEntity(
            username = userDetail.username,
            email = userDetail.email,
            name = userDetail.name,
            phoneNum = userDetail.phoneNum,
            photoUrl = userDetail.photoUrl
        )
    }

    // TODO: fix toUserDetailEntityMap
    fun toUserDetailEntityMap(userDetail: UserDetail): MutableMap<String, Any> {
        val userDetailMap = mutableMapOf<String, Any>()

        with(userDetail) {
            name?.let { userDetailMap.put(USER_NAME_FIELD, it) }
            phoneNum?.let { userDetailMap.put(USER_PHONE_NUM_FIELD, it) }
        }

        return userDetailMap
    }
}