package com.foobarust.android.works

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.foobarust.android.R
import com.foobarust.android.utils.ProgressNotification
import com.foobarust.android.utils.buildProgressNotification
import com.foobarust.domain.repositories.AuthRepository
import com.foobarust.domain.repositories.UserRepository
import com.foobarust.domain.states.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect

/**
 * Created by kevin on 1/8/21
 */

private const val TAG = "UploadUserPhotoWork"

@HiltWorker
class UploadUserPhotoWork @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val notificationManager: NotificationManager,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!authRepository.isUserSignedIn()) {
            return Result.failure()
        }

        val photoUri = inputData.getString(USER_PHOTO_URL) ?: return Result.failure()
        val photoExtension = inputData.getString(USER_PHOTO_EXTENSION) ?: return Result.failure()
        var hasError: Result? = null

        val progressNotification = notificationManager.buildProgressNotification(
            context = context,
            channelId = context.getString(R.string.notification_channel_upload_id),
            title = context.getString(R.string.notification_upload_user_photo_title),
            messageBody = context.getString(R.string.notification_upload_user_photo_body)
        )

        userRepository.uploadUserPhoto(
            userId = authRepository.getUserId(),
            uri = photoUri,
            extension = photoExtension
        ).collect {
            when (it) {
                is Resource.Success -> {
                    Log.d(TAG, "Upload job finished.")
                    clearProgressNotification(progressNotification)
                }
                is Resource.Error -> {
                    Log.d(TAG, "Upload job failed: ${it.message}")
                    hasError = Result.failure()
                    clearProgressNotification(progressNotification)
                }
                is Resource.Loading -> {
                    Log.d(TAG, "Upload job: ${it.progress}")
                    showProgressNotification(progressNotification, it.progress)
                }
            }
        }

        return hasError ?: Result.success()
    }

    private fun showProgressNotification(progressNotification: ProgressNotification, progress: Double?) {
        progressNotification.updateProgress(progress ?: 0.0)
        notificationManager.notify(
            progressNotification.notificationId,
            progressNotification.getNotification()
        )
    }

    private fun clearProgressNotification(progressNotification: ProgressNotification) {
        progressNotification.clear()
        notificationManager.notify(
            progressNotification.notificationId,
            progressNotification.getNotification()
        )
    }

    companion object {
        const val WORK_NAME = "upload_user_photo"
        const val USER_PHOTO_URL = "user_photo_uri"
        const val USER_PHOTO_EXTENSION = "user_photo_extension"
    }
}