package com.foobarust.android.utils

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.foobarust.android.states.UiState

/**
 * Created by kevin on 9/29/20
 */

fun CombinedLoadStates.anyError(): LoadState.Error? {
    return source.append as? LoadState.Error
        ?: source.prepend as? LoadState.Error
        ?: append as? LoadState.Error
        ?: prepend as? LoadState.Error
}

fun LoadState.asUiFetchState(): UiState {
    return when (this) {
        is LoadState.NotLoading -> UiState.Success
        LoadState.Loading -> UiState.Loading
        is LoadState.Error -> UiState.Error(this.error.message)
    }
}