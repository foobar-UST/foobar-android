package com.foobarust.android.shared

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.foobarust.android.R

open class FullScreenDialogFragment : DialogFragment() {

    open var onBackPressed: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                val handler = this@FullScreenDialogFragment.onBackPressed
                if (handler != null) {
                    handler()
                } else {
                    super.onBackPressed()
                }
            }
        }

        if (savedInstanceState == null) {
            dialog.window?.setWindowAnimations(
                R.style.Animation_Foobar_FullScreenDialogFragment
            )
        } else {
            dialog.window?.setWindowAnimations(
                R.style.Animation_Foobar_FullScreenDialogFragment_Restore
            )
        }

        return dialog
    }

    override fun onDestroyView() {
        onBackPressed = null
        super.onDestroyView()
    }
}