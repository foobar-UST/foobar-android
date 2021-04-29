package com.foobarust.android.utils

import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment

/**
 * Created by kevin on 4/29/21
 */
 
fun DialogFragment.setLayoutFullscreen() {
    dialog?.window?.let {
        WindowCompat.setDecorFitsSystemWindows(it, false)
    }
}