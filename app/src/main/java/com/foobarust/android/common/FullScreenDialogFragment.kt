package com.foobarust.android.common

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.foobarust.android.R

open class FullScreenDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.fullscreen_dialog_animation)
    }
}