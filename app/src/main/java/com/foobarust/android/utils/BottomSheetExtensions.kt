package com.foobarust.android.utils

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.foobarust.android.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Created by kevin on 1/27/21
 */

/**
 * Hide the bottom sheet for a given condition.
 */
fun ViewGroup.bottomSheetHideIf(hide: Boolean) {
    BottomSheetBehavior.from(this).run {
        isHideable = true
        state = if (hide) BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_COLLAPSED
        isHideable = false
    }
}

/**
 * Set default material background to a bottom sheet layout.
 */
fun ViewGroup.bottomSheetApplyDefaultBackground() {
    BottomSheetBehavior.from(this)

    background = MaterialShapeDrawable(
        context,
        null,
        R.attr.bottomSheetStyle,
        0
    ).apply {
        fillColor = ColorStateList.valueOf(
            context.themeColor(R.attr.colorSurface)
        )
        elevation = resources.getDimension(R.dimen.elevation_xmedium)

        initializeElevationOverlay(context)
    }
}

/**
 * Set the peek height of the bottom sheet to the minimum height that
 * a specific view can be fully displayed at the bottom.
 */
suspend fun ViewGroup.bottomSheetPeekUntil(toView: View) {
    val behavior = BottomSheetBehavior.from(this)
    // Hide bottom sheet at the beginning
    behavior.peekHeight = 0

    return suspendCancellableCoroutine { continuation ->
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                behavior.setPeekHeight(toView.bottom, true)
            }
        }

        viewTreeObserver.addOnGlobalLayoutListener(listener)

        continuation.invokeOnCancellation {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
}
