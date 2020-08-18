package com.foobarust.android

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.foobarust.android.utils.SingleLiveEvent

/**
 * Created by kevin on 8/9/20
 */
abstract class BaseViewModel : ViewModel() {

    private val _message = SingleLiveEvent<String>()
    val message: LiveData<String>
        get() = _message

    fun showMessage(message: String) {
        _message.value = message
    }

    fun postMessage(message: String) {
        _message.postValue(message)
    }
}