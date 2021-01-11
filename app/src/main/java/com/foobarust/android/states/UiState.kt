package com.foobarust.android.states

/**
 * Created by kevin on 10/14/20
 */

sealed class UiState {

    object Success : UiState()

    object Loading : UiState()

    data class Error(val message: String?) : UiState()
}