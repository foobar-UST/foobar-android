package com.foobarust.domain.repository

/**
 * Created by kevin on 4/9/21
 */

/*
class FakeUserRepositoryImpl : UserRepository {

    private var userDetailCache: UserDetail? = null
    private var userDetailNetwork: UserDetail? = null

    private var shouldReturnNetworkError = false
    private var shouldReturnUnauthorizedError = false

    override fun getUserDetailObservable(userId: String): Flow<Resource<UserDetail>> = flow {
        emit(Resource.Loading())
        when {
            shouldReturnNetworkError -> emit(Resource.Error("Network error."))
            userDetailNetwork == null && userDetailCache == null ->
                emit(Resource.Error("User detail not found."))
            userDetailNetwork == null -> emit(Resource.Error("Using cached user detail."))
            else -> emit(Resource.Success(userDetailNetwork!!))
        }
    }

    override suspend fun updateUserDetail(idToken: String, name: String?, phoneNum: String?) {
        if (shouldReturnNetworkError) {
            throw Exception("Network error.")
        }
        userDetailNetwork = userDetailNetwork?.copy(
            name = name,
            phoneNum = phoneNum
        )
    }

    override suspend fun removeUserDetailCache() {
        userDetailCache = null
    }

    override fun uploadUserPhoto(
        userId: String,
        uri: String,
        extension: String
    ): Flow<Resource<Unit>> {

    }

    override suspend fun getUserCompleteTutorial(): Boolean {

    }

    override suspend fun updateHasUserCompleteTutorial(completed: Boolean) {

    }

    override suspend fun getUserPublicProfile(userId: String): UserPublic {

    }

    override suspend fun getUserDeliveryProfile(userId: String): UserDelivery {

    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    fun setCachedUserDetail(userDetail: UserDetail) {
        userDetailCache = userDetail
    }

    fun setNetworkUserDetail(userDetail: UserDetail) {
        userDetailNetwork = userDetail
    }
}

 */