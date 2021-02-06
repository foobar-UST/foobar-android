package com.foobarust.android.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.foobarust.android.R
import com.foobarust.android.utils.createNotificationChannel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kevin on 12/20/20
 */

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext context: Context,
    private val notificationManager: NotificationManager
) {

    private val notificationChannels: List<NotificationChannel> = listOf(
        NotificationChannel(
            channelId = context.getString(R.string.notification_channel_default_id),
            channelName = context.getString(R.string.notification_channel_default_name),
            importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
        ),
        NotificationChannel(
            channelId = context.getString(R.string.notification_channel_upload_id),
            channelName = context.getString(R.string.notification_channel_upload_name),
            importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
        ),
        NotificationChannel(
            channelId = context.getString(R.string.notification_channel_order_id),
            channelName = context.getString(R.string.notification_channel_order_name),
            importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
        ),
    )

    fun createNotificationChannels() {
        notificationChannels.forEach {
            notificationManager.createNotificationChannel(
                channelId = it.channelId,
                channelName = it.channelName,
                channelDescription = it.channelDescription,
                importance = it.importance
            )
        }
    }
}

data class NotificationChannel(
    val channelId: String,
    val channelName: String,
    val channelDescription: String? = null,
    val importance: Int
)