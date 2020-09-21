package com.foobarust.android.services

import android.app.NotificationManager
import android.util.Log
import com.foobarust.android.splash.SplashActivity
import com.foobarust.android.utils.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MessagingService"

@AndroidEntryPoint
class FoobarFirebaseMessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var notificationManager: NotificationManager

    /**
     * Called when the message is receied
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if the message contains a data payload
        remoteMessage.data.let {

        }

        // Check if the message contains a notification payload
        remoteMessage.notification?.let {
            sendNotification(
                channelId = it.channelId as String,
                title = it.title as String,
                messageBody = it.body as String
            )
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * InstanceID token is initially generated so this is where you would retrieve
     * the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "newToken: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // To be implemented
    }

    private fun sendNotification(channelId: String, title: String, messageBody: String) {
        notificationManager.sendNotification(
            channelId = channelId,
            title = title,
            messageBody = messageBody,
            context = applicationContext,
            intentActivity = SplashActivity::class
        )
    }
}