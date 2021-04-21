package com.foobarust.domain.repository

import com.foobarust.domain.repositories.MessagingRepository
import java.util.*

/**
 * Created by kevin on 4/19/21
 */

class FakeMessagingRepositoryImpl(
    private val idToken: String
) : MessagingRepository {

    private var fakeDeviceToken = UUID.randomUUID().toString()

    private var fakeUserDeviceToken: String? = null

    override suspend fun getDeviceToken(): String = fakeDeviceToken

    override suspend fun uploadDeviceToken(deviceToken: String) {
        fakeDeviceToken = deviceToken
    }

    override suspend fun linkDeviceTokenToUser(idToken: String) {
        if (this.idToken != idToken) throw Exception("Invalid id token.")
        fakeUserDeviceToken = fakeDeviceToken
    }

    override suspend fun unlinkDeviceTokenFromUser(deviceToken: String) {
        fakeUserDeviceToken = null
    }
}