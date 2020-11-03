package com.foobarust.android.states

/**
 * Created by kevin on 10/14/20
 */

sealed class UiFetchState {

    object Success : UiFetchState()

    object Loading : UiFetchState()

    data class Error(val message: String?) : UiFetchState()
}