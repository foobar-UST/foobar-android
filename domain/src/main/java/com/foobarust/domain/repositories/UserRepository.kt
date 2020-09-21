package com.foobarust.domain.repositories

import com.foobarust.domain.models.UserDetailInfo
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/12/20
 */

interface UserRepository {

    fun getUserDetailInfoObservable(uid: String): Flow<Resource<UserDetailInfo>>

    suspend fun updateUserName(uid: String, name: String)

    suspend fun updateUserPhoneNumber(uid: String, phoneNum: String)

    fun updateUserPhoto(uid: String, uriString: String): Flow<Resource<Unit>>
}