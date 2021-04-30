package com.foobarust.android.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.foobarust.android.databinding.FragmentLicenseBinding
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.applySystemWindowInsetsMargin
import com.foobarust.android.utils.applySystemWindowInsetsPadding
import com.foobarust.android.utils.setLayoutFullscreen

class LicenseFragment : FullScreenDialogFragment() {

    private var binding: FragmentLicenseBinding by AutoClearedValue(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setLayoutFullscreen()

        binding = FragmentLicenseBinding.inflate(inflater, container, false).apply {
            appBarLayout.applySystemWindowInsetsPadding(applyTop = true)
            licenseWebView.applySystemWindowInsetsMargin(applyBottom = true)
        }

        // Dismiss dialog
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.licenseWebView.run {
            loadUrl("file:///android_asset/open_source_licenses.html")
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
        }
    }
}