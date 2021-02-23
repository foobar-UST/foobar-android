package com.foobarust.android.utils

import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Created by kevin on 9/29/20
 */

fun CombinedLoadStates.anyError(): LoadState.Error? {
    return source.append as? LoadState.Error
        ?: source.prepend as? LoadState.Error
        ?: append as? LoadState.Error
        ?: prepend as? LoadState.Error
}

fun CombinedLoadStates.updateViews(
    mainLayout: ViewGroup,
    progressBar: ProgressBar,
    errorLayout: ViewGroup? = null,
    swipeRefreshLayout: SwipeRefreshLayout ? = null
) {
    if (swipeRefreshLayout != null) {
        // Show progress bar on start
        progressBar.isVisible = source.refresh is LoadState.Loading &&
            !swipeRefreshLayout.isRefreshing

        swipeRefreshLayout.isEnabled = progressBar.isGone
    } else {
        progressBar.isVisible = source.refresh is LoadState.Loading
    }

    // Stop swipe refreshing when the loading is completed.
    swipeRefreshLayout?.run {
        if (source.refresh is LoadState.NotLoading ||
            source.refresh is LoadState.Error) {
            isRefreshing = false
        }
    }

    // Hide main layout
    mainLayout.isVisible = source.refresh is LoadState.NotLoading ||
        source.refresh !is LoadState.Error ||
        (source.refresh is LoadState.Loading && progressBar.isGone)

    // Show error layout when there is loading error.
    errorLayout?.run {
        isVisible = source.refresh is LoadState.Error && mainLayout.isGone
    }
}