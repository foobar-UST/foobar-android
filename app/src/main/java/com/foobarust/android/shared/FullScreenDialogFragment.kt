package com.foobarust.android.shared

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.foobarust.android.R

open class FullScreenDialogFragment : DialogFragment {

    open var onBackPressed: (() -> Unit)? = null

    private var disableTransition: Boolean = false

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                val handler = onBackPressed
                if (handler != null) {
                    handler()
                } else {
                    super.onBackPressed()
                }
            }
        }
    }

    override fun onStart() {
        if (disableTransition) {
            dialog?.window?.setWindowAnimations(
                R.style.Animation_Foobar_FullScreenDialogFragment_Restore
            )
        } else {
            dialog?.window?.setWindowAnimations(
                R.style.Animation_Foobar_FullScreenDialogFragment
            )
        }

        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        disableTransition = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressed = null
    }
}