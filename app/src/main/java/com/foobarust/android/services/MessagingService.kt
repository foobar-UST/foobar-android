package com.foobarust.android.services

import android.app.NotificationManager
import android.util.Log
import androidx.work.WorkManager
import com.foobarust.android.utils.ResourceIdentifier
import com.foobarust.android.utils.sendImageNotification
import com.foobarust.android.utils.sendNotification
import com.foobarust.domain.di.ApplicationScope
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.messaging.InsertDeviceTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MessagingService"

@AndroidEntryPoint
class MessagingService: FirebaseMessagingService() {

    @ApplicationScope
    @Inject
    lateinit var coroutineScope: CoroutineScope
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var workManager: WorkManager
    @Inject
    lateinit var resourceIdentifier: ResourceIdentifier
    @Inject
    lateinit var insertDeviceTokenUseCase: InsertDeviceTokenUseCase

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { notification ->
            // Required fields
            val channelId = notification.channelId ?: return
            val titleLocId = notification.titleLocalizationKey ?: return
            val link = notification.link ?: return

            val titleLocArgs = notification.titleLocalizationArgs

            val bodyLocId = notification.bodyLocalizationKey
            val imageUrl = notification.imageUrl

            if (imageUrl != null) {
                coroutineScope.launch {
                    notificationManager.sendImageNotification(
                        context = applicationContext,
                        title = resourceIdentifier.getString(titleLocId, titleLocArgs),
                        messageBody = bodyLocId?.let { resourceIdentifier.getString(it) },
                        channelId = notification.channelId.toString(),
                        imageUrl = imageUrl.toString(),
                        link = notification.link.toString()
                    )
                }
            } else {
                notificationManager.sendNotification(
                    context = applicationContext,
                    title = resourceIdentifier.getString(titleLocId, titleLocArgs),
                    messageBody = bodyLocId?.let { resourceIdentifier.getString(it) },
                    channelId = channelId,
                    link = link.toString()
                )
            }
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * InstanceID token is initially generated so this is where you would retrieve
     * the token.
     */
    override fun onNewToken(token: String) {
        coroutineScope.launch {
            insertDeviceTokenUseCase(Unit).collect {
                when (it) {
                    is Resource.Success -> Log.d(TAG, "Uploaded device token.")
                    is Resource.Error -> Log.d(TAG, "Failed to upload device token.")
                    is Resource.Loading -> Log.d(TAG, "Uploading device token.")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}