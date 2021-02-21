package com.foobarust.domain.usecases.user

import androidx.paging.PagingData
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.user.UserNotification
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.usecases.PagingUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Created by kevin on 2/8/21
 */

class GetUserNotificationsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : PagingUseCase<Unit, UserNotification>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<PagingData<UserNotification>> = flow {
        if (!authRepository.isUserSignedIn()) {
            emitAll(flowOf(PagingData.empty<UserNotification>()))
            return@flow
        }

        val userId = authRepository.getUserId()

        emitAll(userRepository.getUserNotificationsPagingData(userId))
    }
}