package com.foobarust.domain.usecases.user

import com.foobarust.domain.di.ApplicationScope
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.MessagingRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 2/7/21
 */

private const val TAG = "DoOnSignInUseCase"

class DoOnSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val messagingRepository: MessagingRepository,
    @ApplicationScope private val coroutineScope: CoroutineScope,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: Unit) {
        // Upload device token
        coroutineScope.launch {
            messagingRepository.linkDeviceTokenToUser(
                idToken = authRepository.getUserIdToken(),
                deviceToken = messagingRepository.getDeviceToken()
            )
            println("[$TAG] Uploaded device token.")
        }

        // Remove saved email
        coroutineScope.launch {
            authRepository.removeSavedAuthEmail()
        }
    }
}