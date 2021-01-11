package com.foobarust.android.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.foobarust.android.states.UiState
import com.foobarust.android.utils.SingleLiveEvent

abstract class BaseViewModel : ViewModel() {

    // Observable for displaying toast message
    private val _toastMessage = SingleLiveEvent<String?>()
    val toastMessage: LiveData<String?>
        get() = _toastMessage

    // Observable for controlling layouts including progress bar and network error layout
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState>
        get() = _uiState.map {
            if (it is UiState.Error) showToastMessage(it.message)
            it
        }

    fun showToastMessage(message: String?) {
        _toastMessage.value = message
    }

    fun setUiState(uiState: UiState) {
        _uiState.value = uiState
    }
}