package com.foobarust.android.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by kevin on 3/21/21
 */

class NotifyingLinearLayoutManager(
    context: Context,
    orientation: Int,
    reverseLayout: Boolean
) : LinearLayoutManager(context, orientation, reverseLayout) {

    private val visibleItemsCount = MutableStateFlow(0)

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)

        // Find number of visible items
        val firstVisibleItemPosition = findFirstVisibleItemPosition()
        val lastVisibleItemPosition = findLastVisibleItemPosition()
        visibleItemsCount.value = lastVisibleItemPosition - firstVisibleItemPosition + 1
    }

    fun getVisibleItemsCount(): StateFlow<Int> = visibleItemsCount.asStateFlow()
}