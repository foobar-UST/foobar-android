package com.foobarust.domain.repositories

import com.foobarust.domain.models.UserDetail
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/12/20
 */

interface UserRepository {

    suspend fun getIsOnboardingCompleted(): Boolean

    suspend fun saveIsOnboardingCompleted(isCompleted: Boolean)

    suspend fun getLocalUserDetail(userId: String): UserDetail?

    suspend fun updateLocalUserDetail(userId: String, userDetail: UserDetail)

    suspend fun removeLocalUserDetail(userId: String)

    suspend fun getRemoteUserDetail(userId: String): UserDetail?

    fun getRemoteUserDetailObservable(userId: String): Flow<Resource<UserDetail>>

    suspend fun updateRemoteUserDetail(userId: String, userDetail: UserDetail)

    fun updateUserPhoto(userId: String, uriString: String): Flow<Resource<Unit>>
}