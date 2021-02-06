package com.foobarust.android.utils

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Created by kevin on 1/27/21
 */

/**
 * Hide the bottom sheet for a given condition.
 */
fun BottomSheetBehavior<*>.hideIf(hide: Boolean) {
    isHideable = true
    state = if (hide) {
        BottomSheetBehavior.STATE_HIDDEN
    } else {
        BottomSheetBehavior.STATE_COLLAPSED
    }
    isHideable = false
}

/**
 * Set the peek height of the bottom sheet to the minimum height that
 * a specific view can be fully displayed at the bottom.
 */

suspend fun bottomSheetPeekTo(bottomSheet: ViewGroup, toView: View) {
    val behavior = BottomSheetBehavior.from(bottomSheet).apply {
        peekHeight = 0
    }

    return suspendCancellableCoroutine { continuation ->
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                bottomSheet.viewTreeObserver.removeOnGlobalLayoutListener(this)
                behavior.setPeekHeight(toView.bottom, true)
            }
        }

        bottomSheet.viewTreeObserver.addOnGlobalLayoutListener(listener)

        continuation.invokeOnCancellation {
            bottomSheet.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
}
