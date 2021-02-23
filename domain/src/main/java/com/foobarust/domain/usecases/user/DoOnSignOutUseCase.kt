package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.MessagingRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 2/7/21
 */

private const val TAG = "DoOnSignOutUseCase"

class DoOnSignOutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val messagingRepository: MessagingRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: Unit) {
        // Remove user detail cache
        userRepository.removeUserDetailCache()
        println("[$TAG] Removed user detail cache.")

        // Unlink user from device token
        val deviceToken = messagingRepository.getDeviceToken()
        messagingRepository.unlinkDeviceTokenFromUser(deviceToken)
        println("[$TAG] Unlinked device token.")
    }
}