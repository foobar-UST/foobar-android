package com.foobarust.data.repositories

import com.foobarust.data.api.RemoteService
import com.foobarust.data.models.user.InsertDeviceTokenRequest
import com.foobarust.data.models.user.LinkDeviceTokenRequest
import com.foobarust.data.models.user.UnlinkDeviceTokenRequest
import com.foobarust.domain.repositories.MessagingRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created by kevin on 2/6/21
 */

class MessagingRepositoryImpl @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging,
    private val remoteService: RemoteService
) : MessagingRepository {

    override suspend fun getDeviceToken(): String {
        return firebaseMessaging.token.await()
    }

    override suspend fun uploadDeviceToken(deviceToken: String) {
        remoteService.insertDeviceToken(
            insertDeviceTokenRequest = InsertDeviceTokenRequest(deviceToken)
        )
    }

    override suspend fun linkDeviceTokenToUser(idToken: String, deviceToken: String) {
        remoteService.linkDeviceToken(
            idToken = idToken,
            linkDeviceTokenRequest = LinkDeviceTokenRequest(deviceToken)
        )
    }

    override suspend fun unlinkDeviceTokenFromUser(deviceToken: String) {
        remoteService.unlinkDeviceToken(
            unlinkDeviceTokenRequest = UnlinkDeviceTokenRequest(deviceToken)
        )
    }
}