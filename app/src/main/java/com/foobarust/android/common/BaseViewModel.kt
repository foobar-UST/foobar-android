package com.foobarust.android.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.foobarust.android.utils.SingleLiveEvent

abstract class BaseViewModel : ViewModel() {

    private val _toastMessage = SingleLiveEvent<String?>()
    val toastMessage: LiveData<String?>
        get() = _toastMessage

    private val _networkError = SingleLiveEvent<Boolean>()
    val networkError: LiveData<Boolean>
        get() = _networkError

    private val _loadingProgress = SingleLiveEvent<Boolean>()
    val loadingProgress: LiveData<Boolean>
        get() = _loadingProgress

    fun showMessage(message: String?) {
        _toastMessage.value = message
    }

    fun postMessage(message: String?) {
        _toastMessage.postValue(message)
    }

    fun showNetworkError() {
        _networkError.value = true
    }

    fun controlLoadingProgress(isShow: Boolean) {
        _loadingProgress.value = isShow
    }
}