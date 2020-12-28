package com.foobarust.android.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.notification.NotificationHelper
import com.foobarust.android.utils.SingleLiveEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_DELAY = 800L

class SplashViewModel @ViewModelInject constructor(
    notificationHelper: NotificationHelper
) : ViewModel() {

    private val _navigateToMain = SingleLiveEvent<Unit>()
    val navigateToMain: LiveData<Unit>
        get() = _navigateToMain

    init {
        notificationHelper.createNotificationChannels()
        navigateToMain()
    }

    private fun navigateToMain() {
        viewModelScope.launch {
            // TODO: Insert delay for better transition
            delay(SPLASH_DELAY)
            _navigateToMain.value = Unit
        }
    }
}


