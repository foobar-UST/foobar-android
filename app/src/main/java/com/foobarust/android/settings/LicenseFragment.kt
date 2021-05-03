package com.foobarust.android.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentLicenseBinding
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.applySystemWindowInsetsPadding
import com.foobarust.android.utils.setLayoutFullscreen
import com.foobarust.android.utils.viewBinding

class LicenseFragment : FullScreenDialogFragment(R.layout.fragment_license) {

    private val binding: FragmentLicenseBinding by viewBinding(FragmentLicenseBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutFullscreen(aboveNavBar = true)

        binding.appBarLayout.applySystemWindowInsetsPadding(applyTop = true)

        // Dismiss dialog
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.licenseWebView.run {
            loadUrl("file:///android_asset/open_source_licenses.html")
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
        }
    }
}