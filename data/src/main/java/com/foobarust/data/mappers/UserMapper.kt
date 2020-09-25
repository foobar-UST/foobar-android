package com.foobarust.data.mappers

import com.foobarust.data.models.UserDoc
import com.foobarust.domain.models.UserDetail
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun toUserDetail(userDoc: UserDoc): UserDetail {
        return UserDetail(
            username = userDoc.username,
            email = userDoc.email,
            name = userDoc.name,
            phoneNum = userDoc.phone_num,
            photoUrl = userDoc.photo_url
        )
    }
}