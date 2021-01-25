package com.foobarust.android.common

/**
 * Created by kevin on 10/14/20
 */

sealed class UiState {

    object Success : UiState()

    object Loading : UiState()

    data class Error(val message: String?) : UiState()
}