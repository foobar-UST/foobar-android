package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.UserDetailInfo
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by kevin on 9/17/20
 */

private const val GET_USER_DETAIL_INFO_NOT_SIGNED_IN = "User is not signed in."

class GetUserDetailInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, UserDetailInfo>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<UserDetailInfo>> = flow {
        // Check if user is signed in
        if (!authRepository.isSignedIn()) {
            emit(Resource.Error(GET_USER_DETAIL_INFO_NOT_SIGNED_IN))
            return@flow
        }

        // Check if photo url is changed and reload auth profile
        val uid = authRepository.getAuthUid()

        emitAll(userRepository.getUserDetailInfoObservable(uid)
            .onEach {
                if (it is Resource.Success) {
                    checkAndUpdateAuthProfilePhoto(
                        authPhotoUrl = authRepository.getAuthProfileObservable().first().getSuccessDataOr(null)?.photoUrl,
                        userPhotoUrl = it.data.photoUrl
                    )
                }
            }
        )
    }

    private suspend fun checkAndUpdateAuthProfilePhoto(authPhotoUrl: String?, userPhotoUrl: String?) {
        // Update auth profile photo
        if (authPhotoUrl != userPhotoUrl) {
            authRepository.reloadAuthInfo()
        }
    }
}