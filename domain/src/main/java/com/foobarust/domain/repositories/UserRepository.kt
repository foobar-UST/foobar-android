package com.foobarust.domain.repositories

import com.foobarust.domain.models.user.UserDelivery
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/12/20
 */

interface UserRepository {

    fun getUserDetailObservable(userId: String): Flow<Resource<UserDetail>>

    suspend fun updateUserDetail(idToken: String, name: String?, phoneNum: String?)

    suspend fun removeUserDetailCache()

    fun uploadUserPhoto(userId: String, uri: String, extension: String): Flow<Resource<Unit>>

    suspend fun getUserCompleteTutorial(): Boolean

    suspend fun updateHasUserCompleteTutorial(completed: Boolean)

    suspend fun getUserPublicProfile(userId: String): UserPublic

    suspend fun getUserDeliveryProfile(userId: String): UserDelivery
}