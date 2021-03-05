package com.foobarust.android.shared

import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.utils.CustomTabHelper
import com.foobarust.android.utils.showShortToast
import com.foobarust.android.utils.themeColor

/**
 * Created by kevin on 3/1/21
 */

class ActionViewDialog : DialogFragment() {

    private val navArgs: ActionViewDialogArgs by navArgs()

    override fun onStart() {
        super.onStart()

        dismiss()

        val launchResult = CustomTabHelper.launchCustomTab(
            context = requireContext(),
            url = navArgs.uri,
            tabColorInt = requireContext().themeColor(R.attr.colorPrimarySurface)
        )

        if (!launchResult) {
            showShortToast(getString(R.string.error_resolve_activity_failed))
        }
    }
}