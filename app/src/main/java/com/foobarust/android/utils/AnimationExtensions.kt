package com.foobarust.android.utils

import android.view.View
import com.foobarust.android.R

/**
 * Created by kevin on 10/30/20
 */

fun View.slideUp(duration: Int = resources.getInteger(R.integer.motion_duration_default)) {
    animate()
        .withStartAction { visibility = View.VISIBLE }
        .translationY(0f)
        .duration = duration.toLong()
}

fun View.slideDown(duration: Int = resources.getInteger(R.integer.motion_duration_default)) {
    animate()
        .translationY(height.toFloat())
        .setDuration(duration.toLong())
        .withEndAction { visibility = View.GONE }
}