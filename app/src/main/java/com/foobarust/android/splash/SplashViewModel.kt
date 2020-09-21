package com.foobarust.android.splash

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.main.MainActivity
import com.foobarust.android.onboarding.OnboardingActivity
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.android.utils.createNotificationChannel
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.onboarding.GetOnboardingCompletedUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

const val SPLASH_DELAY = 800L

class SplashViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager,
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase
) : BaseViewModel() {

    // TODO: Create a list of notification channels
    private val notificationChannels: List<NotificationChannel> = listOf(
        NotificationChannel(
            channelId = context.getString(R.string.foobar_default_notification_channel_id),
            channelName = context.getString(R.string.foobar_default_notification_channel_name),
            importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
        ),
        NotificationChannel(
            channelId = context.getString(R.string.foobar_upload_notification_channel_id),
            channelName = context.getString(R.string.foobar_upload_notification_channel_name),
            importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
        ),
    )

    private val _startNavigation = SingleLiveEvent<KClass<*>>()
    val startNavigation: LiveData<KClass<*>>
        get() = _startNavigation

    init {
        createNotificationChannels()
        insertDelayAndStartNavigation()
    }

    private fun insertDelayAndStartNavigation() {
        viewModelScope.launch {
            // TODO: Insert delay for better transition
            delay(SPLASH_DELAY)

            // Navigate to MainActivity if onboarding has been completed.
            // Otherwise, navigate to OnboardingActivity to complete the onboarding process.
            _startNavigation.value = when {
                getOnboardingCompletedUseCase(Unit).getSuccessDataOr(false) -> MainActivity::class
                else -> OnboardingActivity::class
            }
        }
    }

    private fun createNotificationChannels() {
        notificationChannels.forEach {
            notificationManager.createNotificationChannel(
                channelId = it.channelId,
                channelName = it.channelName,
                channelDescription = it.channelDescription,
                importance = it.importance
            )
        }
    }

    data class NotificationChannel(
        val channelId: String,
        val channelName: String,
        val channelDescription: String? = null,
        val importance: Int
    )
}


