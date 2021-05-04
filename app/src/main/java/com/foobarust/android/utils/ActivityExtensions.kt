package com.foobarust.android.utils

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

/**
 * Created by kevin on 4/29/21
 */

fun AppCompatActivity.setLayoutFullscreen(aboveNavBar: Boolean = false) {
    WindowCompat.setDecorFitsSystemWindows(window, false)

    if (aboveNavBar) {
        window.decorView.findViewById<ViewGroup>(android.R.id.content)
            .applySystemWindowInsetsMargin(applyBottom = true)
    }
}