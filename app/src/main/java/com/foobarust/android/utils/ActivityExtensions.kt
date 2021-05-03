package com.foobarust.android.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

/**
 * Created by kevin on 4/29/21
 */

fun AppCompatActivity.setLayoutFullscreen() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
}