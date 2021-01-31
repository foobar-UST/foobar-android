package com.foobarust.android.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.utils.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach

abstract class BaseViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: LiveData<UiState> = _uiState
        .onEach {
            if (it is UiState.Error) {
                showToastMessage(it.message)
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    private val _toastMessage = SingleLiveEvent<String?>()
    val toastMessage: LiveData<String?>
        get() = _toastMessage

    fun showToastMessage(message: String?) {
        _toastMessage.value = message
    }

    fun setUiState(uiState: UiState) {
        _uiState.value = uiState
    }
}


sealed class UiState {
    object Success : UiState()
    object Loading : UiState()
    data class Error(val message: String?) : UiState()
}