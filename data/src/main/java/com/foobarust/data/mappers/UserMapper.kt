package com.foobarust.data.mappers

import com.foobarust.data.models.UserDoc
import com.foobarust.domain.models.UserDetailInfo
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun toUserDetailInfo(userDoc: UserDoc): UserDetailInfo {
        return UserDetailInfo(
            username = userDoc.username,
            email = userDoc.email,
            name = userDoc.name,
            phoneNum = userDoc.phone_num,
            photoUrl = userDoc.photo_url
        )
    }
}