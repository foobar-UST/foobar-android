package com.foobarust.android.utils

import android.app.Activity
import androidx.core.view.WindowCompat

/**
 * Created by kevin on 4/29/21
 */

fun Activity.setLayoutFullscreen() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
}