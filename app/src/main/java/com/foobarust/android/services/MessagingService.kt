package com.foobarust.android.services

import android.app.NotificationManager
import androidx.work.*
import com.foobarust.android.utils.ResourceIdentifier
import com.foobarust.android.utils.sendImageNotification
import com.foobarust.android.utils.sendNotification
import com.foobarust.android.works.UploadDeviceTokenWork
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

private const val TAG = "MessagingService"

@AndroidEntryPoint
class MessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var workManager: WorkManager
    @Inject
    lateinit var resourceIdentifier: ResourceIdentifier

    private val coroutineScope = CoroutineScope(SupervisorJob())

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { notification ->
            val channelId = notification.channelId ?: return@let
            val titleLocId = notification.titleLocalizationKey ?: return@let
            val bodyLocId = notification.bodyLocalizationKey ?: return@let
            val link = notification.link ?: return@let

            val imageUrl = notification.imageUrl

            // String resources arguments
            val titleLocArgs = notification.titleLocalizationArgs
            val bodyLocArgs = notification.bodyLocalizationArgs

            // Resolve notification string contents
            val notificationTitle = resourceIdentifier.getString(titleLocId, titleLocArgs)
            val notificationBody = resourceIdentifier.getString(bodyLocId, bodyLocArgs)

            if (imageUrl != null) {
                coroutineScope.launch {
                    notificationManager.sendImageNotification(
                        context = applicationContext,
                        title = notificationTitle,
                        messageBody = notificationBody,
                        channelId = notification.channelId.toString(),
                        imageUrl = imageUrl.toString(),
                        link = notification.link.toString()
                    )
                }
            } else {
                notificationManager.sendNotification(
                    context = applicationContext,
                    title = notificationTitle,
                    messageBody = notificationBody,
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
        // Upload device token using WorkManager,
        // the work will retried until network is available.
        val inputData = workDataOf(UploadDeviceTokenWork.DEVICE_TOKEN to token)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadDeviceTokenWork>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        workManager.beginUniqueWork(
            UploadDeviceTokenWork.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            uploadRequest
        ).enqueue()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}