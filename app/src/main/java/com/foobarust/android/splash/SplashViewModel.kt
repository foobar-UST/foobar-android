package com.foobarust.android.splash

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.overview.OverviewActivity
import com.foobarust.android.signin.SignInActivity
import com.foobarust.android.utils.SingleLiveEvent
import com.foobarust.android.utils.createNotificationChannel
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.auth.GetSkippedSignInUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * Created by kevin on 8/26/20
 */

const val SPLASH_DELAY = 800L

class SplashViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManager,
    private val firebaseAuth: FirebaseAuth,
    private val getSkippedSignInUseCase: GetSkippedSignInUseCase
) : BaseViewModel() {

    // TODO: Create a list of notification channels
    private val notificationChannels: List<NotificationChannel> = listOf(
        NotificationChannel(
            channelId = context.getString(R.string.foobar_default_notification_channel_id),
            channelName = context.getString(R.string.foobar_default_notification_channel_name),
            importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
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

            _startNavigation.value = when {
                firebaseAuth.currentUser != null ||
                getSkippedSignInUseCase(Unit).getSuccessDataOr(false) -> OverviewActivity::class
                else -> SignInActivity::class
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


