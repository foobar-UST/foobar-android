package com.foobarust.android.utils

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.foobarust.android.states.UiFetchState

/**
 * Created by kevin on 9/29/20
 */

fun CombinedLoadStates.anyError(): LoadState.Error? {
    return source.append as? LoadState.Error
        ?: source.prepend as? LoadState.Error
        ?: append as? LoadState.Error
        ?: prepend as? LoadState.Error
}

fun LoadState.asUiFetchState(): UiFetchState {
    return when (this) {
        is LoadState.NotLoading -> UiFetchState.Success
        LoadState.Loading -> UiFetchState.Loading
        is LoadState.Error -> UiFetchState.Error(this.error.message)
    }
}