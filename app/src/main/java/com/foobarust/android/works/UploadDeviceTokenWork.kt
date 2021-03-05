package com.foobarust.android.works

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.foobarust.domain.repositories.MessagingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Created by kevin on 2/27/21
 */

private const val TAG = "UploadDeviceTokenWork"

@HiltWorker
class UploadDeviceTokenWork @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val messagingRepository: MessagingRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val deviceToken = inputData.getString(DEVICE_TOKEN) ?: return Result.failure()
        var hasError: Result? = null

        try {
            messagingRepository.uploadDeviceToken(deviceToken)
        } catch (e: Exception) {
            Log.d(TAG, "Upload device token failed: ${e.message}")
            hasError = Result.failure()
        }

        return hasError ?: Result.success()
    }

    companion object {
        const val WORK_NAME = "upload_device_token"
        const val DEVICE_TOKEN = "device_token"
    }
}