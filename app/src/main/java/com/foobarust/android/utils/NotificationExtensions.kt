package com.foobarust.android.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.*
import com.foobarust.android.R
import kotlin.reflect.KClass

/**
 * Created by kevin on 8/31/20
 */

const val MAX_PROGRESS = 100
const val MIN_PROGRESS = 0

fun NotificationManager.createNotificationChannel(
    channelId: String,
    channelName: String,
    channelDescription: String?,
    importance: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            importance
        )

        channelDescription?.let {
            notificationChannel.description = it
        }

        this.createNotificationChannel(notificationChannel)
    }
}

fun NotificationManager.generateNotificationId(): Int {
    return System.currentTimeMillis().toInt()
}

fun NotificationManager.sendNotification(
    context: Context,
    intentActivity: KClass<*>,
    channelId: String,
    title: String,
    messageBody: String,
    priority: Int = PRIORITY_DEFAULT,
    autoCancel: Boolean = true,
    @DrawableRes largeIconRes: Int? = null,
    bigPictureStyle: Boolean = false
) {
    val notificationId = generateNotificationId()
    val contentIntent = Intent(context, intentActivity.java)
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        notificationId,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Create notification builder
    val builder = Builder(
        context,
        channelId
    )
        .setSmallIcon(R.drawable.ic_restaurant)         // TODO: Change small icon
        .setContentTitle(title)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setPriority(priority)
        .setAutoCancel(autoCancel)

    // Create large icon
    largeIconRes?.let {
        val decodedImage = BitmapFactory.decodeResource(context.resources, it)
        builder.setLargeIcon(decodedImage)

        // Create big picture style
        if (bigPictureStyle) {
            val bigPicStyle = BigPictureStyle()
                .bigPicture(decodedImage)
                .bigLargeIcon(null)

            builder.setStyle(bigPicStyle)
        }
    }

    // Add actions

    notify(notificationId, builder.build())
}

/**
 * Return the Notification builder of a progress notification
 */
fun NotificationManager.buildProgressNotification(
    context: Context,
    intentActivity: KClass<*>,
    channelId: String,
    title: String,
    messageBody: String,
    priority: Int = PRIORITY_DEFAULT,
    autoCancel: Boolean = true
): ProgressNotification {
    val notificationId = generateNotificationId()
    val contentIntent = Intent(context, intentActivity.java)
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        notificationId,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Create notification builder
    val builder = Builder(
        context,
        channelId
    )
        .setSmallIcon(R.drawable.ic_restaurant)                     // TODO: Change small icon
        .setContentTitle(title)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setPriority(priority)
        .setAutoCancel(autoCancel)
        .setProgress(MAX_PROGRESS, MIN_PROGRESS, false) // Initialize progress

    return ProgressNotification(
        notificationId = notificationId,
        builder = builder
    )
}

fun NotificationCompat.Builder.setCurrentProgress(progress: Double) {
    setProgress(MAX_PROGRESS, progress.toInt(), false)
}

fun NotificationCompat.Builder.clearProgress(resultMessage: String) {
    setContentText(resultMessage)
    setProgress(MIN_PROGRESS, MIN_PROGRESS, false)
}

data class ProgressNotification(
    val notificationId: Int,
    val builder: Builder
)