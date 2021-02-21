package com.foobarust.android.shared

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel : ViewModel() {

    private val _toastMessage = Channel<String>()
    val toastMessage: Flow<String> = _toastMessage.receiveAsFlow()

    fun showToastMessage(message: String?) = message?.let {
        _toastMessage.offer(it)
    }
}