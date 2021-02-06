package com.foobarust.android.splash

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.notification.NotificationHelper
import com.foobarust.android.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 2/5/21
 */

@HiltViewModel
class SplashViewModel @Inject constructor(
    notificationHelper: NotificationHelper,
    private val dynamicLinksUtils: DynamicLinksUtils
) : ViewModel() {

    private val _navigateToMain = SingleLiveEvent<Uri?>()
    val navigateToMain: LiveData<Uri?>
        get() = _navigateToMain

    init {
        notificationHelper.createNotificationChannels()
    }

    fun onDispatchDynamicLinks(link: Uri?) {
        viewModelScope.launch {
            if (link != null) {
                val deepLink = dynamicLinksUtils.extractDeepLink(link)
                _navigateToMain.value = deepLink
            } else {
                _navigateToMain.value = null
            }
        }
    }
}