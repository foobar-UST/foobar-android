package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 11/1/20
 */

// Conditions that require deletion
// 1. There is a copy of local data but no remote data is retrieved

// Conditions that require update to local data
// 1. The updated time of remote data is after that of local data
// 2. No local data is found

// Conditions that require update to remote data
// 1. The updated time of local data is after that of remote data

private const val ERROR_GET_LOCAL_REMOTE_DATA = "Error getting user data."

class SyncUserDetailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
): CoroutineUseCase<Unit, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: Unit) {
        /*
        if (!authRepository.isSignedIn()) {
            throw Exception(ERROR_USER_NOT_SIGNED_IN)
        }

        val userId = authRepository.getAuthUserId()

        val localUserDetail = userRepository.getLocalUserDetail(userId)
        val remoteUserDetail = userRepository.getRemoteUserDetail(userId)

        // Unknown condition
        if (localUserDetail == null && remoteUserDetail == null) {
            throw Exception(ERROR_GET_LOCAL_REMOTE_DATA)
        }

        // Perform deletion
        if (localUserDetail != null && remoteUserDetail == null) {
            userRepository.removeLocalUserDetail(userId)
            return
        }

        // Perform local data update
        if (localUserDetail == null && remoteUserDetail != null) {
            userRepository.updateLocalUserDetail(userId, remoteUserDetail)
            return
        }

        if (localUserDetail != null && remoteUserDetail != null) {
            if (remoteUserDetail.updatedAt > localUserDetail.updatedAt) {
                userRepository.updateLocalUserDetail(userId, remoteUserDetail)
            } else if (localUserDetail.updatedAt > remoteUserDetail.updatedAt) {
                userRepository.updateRemoteUserDetail(userId, localUserDetail)
            }
        }

         */
    }
}