package com.foobarust.domain.repositories

/**
 * Created by kevin on 2/6/21
 */

interface MessagingRepository {

    suspend fun getDeviceToken(): String

    suspend fun uploadDeviceToken(deviceToken: String)

    suspend fun linkDeviceTokenToUser(idToken:String)

    suspend fun unlinkDeviceTokenFromUser(deviceToken: String)
}