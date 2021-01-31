package com.foobarust.android.utils

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Created by kevin on 1/27/21
 */

/**
 * Apply tint to a drawable.
 */
fun Drawable.setTintCompat(@ColorInt color: Int) {
    DrawableCompat.setTint(
        DrawableCompat.wrap(this),
        color
    )
}