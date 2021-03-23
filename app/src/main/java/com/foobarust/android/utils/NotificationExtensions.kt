package com.foobarust.android.utils

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.foobarust.android.R
import com.foobarust.android.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by kevin on 8/31/20
 */

const val MAX_PROGRESS = 100
const val MIN_PROGRESS = 0

/**
 * Create notification channel for > Oreo.
 */

@TargetApi(Build.VERSION_CODES.O)
fun NotificationManager.createNotificationChannel(
    channelId: String,
    channelName: String,
    channelDescription: String? = null,
    importance: Int = NotificationManager.IMPORTANCE_DEFAULT
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

        createNotificationChannel(notificationChannel)
    }
}

fun NotificationManager.sendSimpleNotification(
    context: Context,
    title: String,
    messageBody: String?,
    channelId: String,
    link: String? = null
) {
    val notificationId = generateNotificationId()
    val builder = createDefaultNotificationBuilder(
        context, notificationId, title, messageBody, channelId, link
    )

    notify(notificationId, builder.build())
}

suspend fun NotificationManager.sendImageNotification(
    context: Context,
    title: String,
    messageBody: String?,
    channelId: String,
    imageUrl: String,
    bigPictureStyle: Boolean = false,
    link: String? = null,
) = withContext(Dispatchers.IO) {
    val notificationId = generateNotificationId()
    val builder = createDefaultNotificationBuilder(
        context, notificationId, title, messageBody, channelId, link
    )
    val bitmap = downloadNotificationImage(context, imageUrl)

    if (bigPictureStyle) {
        val bigPicStyle = BigPictureStyle().bigPicture(bitmap)
        builder.setStyle(bigPicStyle)
    } else {
        builder.setLargeIcon(bitmap)
    }

    notify(notificationId, builder.build())
}

/**
 * Return the Notification builder of a progress notification
 */
fun NotificationManager.buildProgressNotification(
    context: Context,
    title: String,
    messageBody: String?,
    channelId: String,
    link: String? = null
): ProgressNotification {
    val notificationId = generateNotificationId()
    val builder = createDefaultNotificationBuilder(
        context, notificationId, title, messageBody, channelId, link
    ).apply {
        setProgress(MAX_PROGRESS, MIN_PROGRESS, false)
    }

    return ProgressNotification(
        notificationId = notificationId,
        builder = builder
    )
}

private suspend fun downloadNotificationImage(
    context: Context,
    imageUrl: String
): Bitmap = suspendCoroutine { continuation ->
    val callback = object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            continuation.resume(resource)
        }
        override fun onLoadCleared(placeholder: Drawable?) = Unit
    }

    Glide.with(context)
        .asBitmap()
        .centerCrop()
        .load(imageUrl)
        .into(callback)
}

private fun createDefaultNotificationBuilder(
    context: Context,
    notificationId: Int,
    title: String,
    messageBody: String?,
    channelId: String,
    link: String?
): Builder {
    val builder = Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_restaurant)
        .setContentTitle(title)
        .setContentText(messageBody)
        .setPriority(PRIORITY_DEFAULT)
        .setAutoCancel(true)

    // Set deep link
    link?.let {
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(link)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder.setContentIntent(contentPendingIntent)
    }

    return builder
}

private fun generateNotificationId(): Int {
    return System.currentTimeMillis().toInt()
}

data class ProgressNotification(
    val notificationId: Int,
    private val builder: Builder
) {
    fun updateProgress(progress: Double) {
        builder.setProgress(MAX_PROGRESS, progress.toInt(), false)
    }

    fun getNotification() = builder.build()
}