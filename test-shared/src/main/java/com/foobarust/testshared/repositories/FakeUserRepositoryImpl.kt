package com.foobarust.testshared.repositories

import com.foobarust.domain.models.user.UserDelivery
import com.foobarust.domain.models.user.UserDetail
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.*

/**
 * Created by kevin on 4/9/21
 */

class FakeUserRepositoryImpl(
    private val idToken: String,
    private val defaultUserDetail: UserDetail,
    private var hasCompletedTutorial: Boolean
) : UserRepository {

    private var shouldReturnNetworkError = false
    private var shouldReturnIOError = false

    private val _userDetailFlow = MutableStateFlow(defaultUserDetail)

    override fun getUserDetailObservable(userId: String): Flow<Resource<UserDetail>> = flow {
        emit(Resource.Loading())
        if (shouldReturnNetworkError) {
            emit(Resource.Error("Network error."))
        } else {
            emitAll(_userDetailFlow.map { Resource.Success(it) })
        }
    }

    override suspend fun updateUserDetail(idToken: String, name: String?, phoneNum: String?) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        if (this.idToken != idToken) throw Exception("Invalid id token.")
        _userDetailFlow.value = _userDetailFlow.value.copy(
            name = name, phoneNum = phoneNum
        )
    }

    override suspend fun removeUserDetailCache() {
        if (shouldReturnIOError) throw Exception("IO error.")
    }

    override fun uploadUserPhoto(
        userId: String,
        uri: String,
        extension: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        if (shouldReturnNetworkError) {
            emit(Resource.Error("Network error."))
        } else {
            emit(Resource.Success(Unit))
        }
    }

    override suspend fun getUserCompleteTutorial(): Boolean {
        if (shouldReturnIOError) throw Exception("IO error.")
        return hasCompletedTutorial
    }

    override suspend fun updateHasUserCompleteTutorial(completed: Boolean) {
        if (shouldReturnIOError) throw Exception("IO error.")
        hasCompletedTutorial = completed
    }

    override suspend fun getUserPublicProfile(userId: String): UserPublic {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return UserPublic(
            id = defaultUserDetail.id,
            username = defaultUserDetail.username,
            photoUrl = defaultUserDetail.photoUrl
        )
    }

    override suspend fun getUserDeliveryProfile(userId: String): UserDelivery {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        return UserDelivery(
            id = defaultUserDetail.id,
            name = defaultUserDetail.name,
            photoUrl = defaultUserDetail.photoUrl,
            phoneNum = defaultUserDetail.phoneNum
        )
    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    fun setIOError(value: Boolean) {
        shouldReturnIOError = value
    }
}