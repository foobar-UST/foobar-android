package com.foobarust.domain.repositories

import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/12/20
 */

interface UserRepository {

    suspend fun getOnboardingCompleted(): Boolean

    suspend fun updateOnboardingCompleted(completed: Boolean)

    /**
     * Get a observable [UserDetail] from local or network db.
     * @param userId the id of the user
     */
    fun getUserDetailObservable(userId: String): Flow<Resource<UserDetail>>

    suspend fun updateUserDetail(idToken: String, name: String?, phoneNum: String?)

    suspend fun clearUserDetailCache()

    fun uploadUserPhoto(userId: String, uri: String, extension: String): Flow<Resource<Unit>>

    suspend fun getUserPublicInfo(userId: String): UserPublic
}