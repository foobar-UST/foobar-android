package com.foobarust.android.notification

import android.app.NotificationManager
import android.content.Context
import com.foobarust.android.R
import com.foobarust.android.utils.createNotificationChannel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kevin on 12/20/20
 */

@Singleton
class NotificationUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager
) {
    fun createNotificationChannels() {
        // Default channel
        notificationManager.createNotificationChannel(
            channelId = context.getString(R.string.notification_channel_default_id),
            channelName = context.getString(R.string.notification_channel_default_name)
        )

        // Upload channel
        notificationManager.createNotificationChannel(
            channelId = context.getString(R.string.notification_channel_upload_id),
            channelName = context.getString(R.string.notification_channel_upload_name)
        )

        // Order update channel
        notificationManager.createNotificationChannel(
            channelId = context.getString(R.string.notification_channel_order_id),
            channelName = context.getString(R.string.notification_channel_order_name)
        )
    }
}