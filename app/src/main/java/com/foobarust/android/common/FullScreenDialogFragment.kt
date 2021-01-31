package com.foobarust.android.common

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.foobarust.android.R

open class FullScreenDialogFragment : DialogFragment() {

    /**
     * Expose the dialog's onBackPressed() method.
     */
    open var onBackPressed: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight)
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