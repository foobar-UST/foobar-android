package com.foobarust.android.utils

import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Created by kevin on 1/27/21
 */

fun RecyclerView.touchOutsideItemsFlow(): Flow<Unit> = callbackFlow {
    val listener = object : RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (e.action != MotionEvent.ACTION_UP) return false
            val child = rv.findChildViewUnder(e.x, e.y)
            return if (child != null) {
                false
            } else {
                channel.offer(Unit)
                true
            }
        }
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit
    }

    addOnItemTouchListener(listener)

    awaitClose { removeOnItemTouchListener(listener) }
}

/**
 * Scroll to top when there is new items inserted at the top of [RecyclerView].
 */
suspend fun <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.scrollToTopWhenFirstItemInserted(
    recyclerView: RecyclerView
) {
    return suspendCancellableCoroutine { continuation ->
        val observer = object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                // Check if the new item is inserted in the first position
                if (positionStart == 0 &&
                    positionStart == layoutManager.findFirstCompletelyVisibleItemPosition()
                ) {
                    // Flicking if using the smooth version, async list animation will do the rest
                    recyclerView.scrollToTop()
                    unregisterAdapterDataObserver(this)
                    continuation.resume(Unit)
                }
            }
        }

        registerAdapterDataObserver(observer)

        continuation.invokeOnCancellation {
            unregisterAdapterDataObserver(observer)
        }
    }
}

fun RecyclerView.scrollToTop() = scrollToPosition(0)

fun RecyclerView.smoothScrollToTop() = smoothScrollToPosition(0)