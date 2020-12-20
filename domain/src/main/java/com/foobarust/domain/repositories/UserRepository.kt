package com.foobarust.domain.repositories

import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/12/20
 */

interface UserRepository {

    suspend fun getOnboardingCompleted(): Boolean

    suspend fun updateOnboardingCompleted(completed: Boolean)

    fun getRemoteUserDetailObservable(userId: String): Flow<Resource<UserDetail>>

    suspend fun updateRemoteUserDetail(userId: String, userDetail: UserDetail)

    fun updateUserPhoto(userId: String, uriString: String): Flow<Resource<Unit>>
}