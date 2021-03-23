package com.foobarust.android

import android.app.Application
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.foobarust.android.utils.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Created by kevin on 8/26/20
 */

@HiltAndroidApp
class FoobarApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun createNotificationChannels() {
        // Default channel
        notificationManager.createNotificationChannel(
            channelId = getString(R.string.notification_channel_default_id),
            channelName = getString(R.string.notification_channel_default_name)
        )

        // Upload channel
        notificationManager.createNotificationChannel(
            channelId = getString(R.string.notification_channel_upload_id),
            channelName = getString(R.string.notification_channel_upload_name)
        )

        // Order update channel
        notificationManager.createNotificationChannel(
            channelId = getString(R.string.notification_channel_order_id),
            channelName = getString(R.string.notification_channel_order_name)
        )

        // Promotion channel
        notificationManager.createNotificationChannel(
            channelId = getString(R.string.notification_channel_promotion_id),
            channelName = getString(R.string.notification_channel_promotion_name)
        )
    }
}