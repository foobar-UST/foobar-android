package com.foobarust.domain.repositories

import com.foobarust.domain.models.UserDetail
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/12/20
 */

interface UserRepository {

    fun getUserDetailObservable(userId: String): Flow<Resource<UserDetail>>

    suspend fun updateUserName(userId: String, name: String)

    suspend fun updateUserPhoneNumber(userId: String, phoneNum: String)

    fun updateUserPhoto(userId: String, uriString: String): Flow<Resource<Unit>>
}