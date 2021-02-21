package com.foobarust.android.utils

import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by kevin on 1/3/21
 */

class ScrollStatesManager {

    private val scrollStates = mutableMapOf<Int, Parcelable?>()

    fun saveScrollState(layoutPosition: Int, recyclerView: RecyclerView) {
        scrollStates[layoutPosition] = recyclerView.layoutManager?.onSaveInstanceState()
    }

    fun restoreScrollState(layoutPosition: Int, recyclerView: RecyclerView) {
        recyclerView.layoutManager?.onRestoreInstanceState(scrollStates[layoutPosition])
    }
}