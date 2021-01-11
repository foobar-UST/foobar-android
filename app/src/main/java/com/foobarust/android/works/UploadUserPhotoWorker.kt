package com.foobarust.android.works

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.foobarust.android.R
import com.foobarust.android.main.MainActivity
import com.foobarust.android.utils.ProgressNotification
import com.foobarust.android.utils.buildProgressNotification
import com.foobarust.android.utils.clearProgress
import com.foobarust.android.utils.setCurrentProgress
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by kevin on 1/8/21
 */

class UploadUserPhotoWorker @WorkerInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val notificationManager: NotificationManager,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!authRepository.isSignedIn()) {
            return Result.failure()
        }

        val photoUri = inputData.getString(USER_PHOTO_URL) ?: return Result.failure()
        val photoExtension = inputData.getString(USER_PHOTO_EXTENSION) ?: return Result.failure()
        var hasError: Result? = null

        // Create progress notification
        val progressNotification = notificationManager.buildProgressNotification(
            context = context,
            intentActivity = MainActivity::class,
            channelId = context.getString(R.string.notification_channel_upload_id),
            title = context.getString(R.string.notification_item_upload_user_photo_title),
            messageBody = context.getString(R.string.notification_item_upload_user_photo_body)
        )

        // Suspend the coroutine until the upload job is finished
        withContext(coroutineDispatcher) {
            launch {
                userRepository.uploadUserPhoto(
                    userId = authRepository.getUserId(),
                    uri = photoUri,
                    extension = photoExtension
                ).collect {
                    Log.d("UploadUserPhotoWorker", it.toString())
                    when (it) {
                        is Resource.Success -> {
                            clearProgressNotification(progressNotification)
                        }
                        is Resource.Error -> {
                            hasError = Result.failure()
                            clearProgressNotification(progressNotification)
                        }
                        is Resource.Loading -> {
                            showProgressNotification(progressNotification, it.progress)
                        }
                    }
                }
            }.join()
        }

        Log.d("UploadUserPhotoWorker", "Upload job finished.")

        return hasError ?: Result.success()
    }

    private fun showProgressNotification(notification: ProgressNotification, progress: Double?) {
        notification.builder.setCurrentProgress(progress ?: 0.0)
        notificationManager.notify(
            notification.notificationId,
            notification.builder.build()
        )
    }

    private fun clearProgressNotification(notification: ProgressNotification) {
        notification.builder.clearProgress(
            context.getString(R.string.notification_item_upload_user_photo_complete)
        )
        notificationManager.notify(
            notification.notificationId,
            notification.builder.build()
        )
    }

    companion object {
        const val WORK_NAME = "upload_user_photo"
        const val USER_PHOTO_URL = "user_photo_uri"
        const val USER_PHOTO_EXTENSION = "user_photo_extension"
    }
}