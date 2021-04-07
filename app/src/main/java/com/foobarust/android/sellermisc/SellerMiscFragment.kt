package com.foobarust.android.sellermisc

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerMiscBinding
import com.foobarust.android.shared.AppConfig.MAP_ZOOM_LEVEL
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/11/20
 */

@AndroidEntryPoint
class SellerMiscFragment : FullScreenDialogFragment() {

    private var binding: FragmentSellerMiscBinding by AutoClearedValue(this)
    private val viewModel: SellerMiscViewModel by viewModels()
    private val navArgs: SellerMiscFragmentArgs by navArgs()
    private var bottomSheetBehavior: BottomSheetBehavior<*> by AutoClearedValue(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onFetchSellerMisc(sellerId = navArgs.sellerId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerMiscBinding.inflate(inflater, container, false).apply {
            root.applyLayoutFullscreen()
            toolbarLayout.applySystemWindowInsetsPadding(applyTop = true)
            bottomSheet.applySystemWindowInsetsMargin(applyTop = true)
            miscLayout.applySystemWindowInsetsPadding(applyBottom = true)
            phoneNumTextView.drawableFitVertical()
            websiteTextView.drawableFitVertical()
        }

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Setup bottom sheet
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        with(binding.bottomSheet) {
            background = MaterialShapeDrawable(context, null,
                R.attr.bottomSheetStyle, 0
            ).apply {
                fillColor = ColorStateList.valueOf(
                    context.themeColor(R.attr.colorSurface)
                )
                elevation = resources.getDimension(R.dimen.elevation_xmedium)
                initializeElevationOverlay(context)
            }
        }

        // Set bottom sheet peek height
        viewLifecycleOwner.lifecycleScope.launch {
            bottomSheetPeekTo(
                bottomSheet = binding.bottomSheet,
                toView = binding.headerGroup
            )
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sellerMiscUiState.collect { uiState ->
                bottomSheetBehavior.hideIf(uiState !is SellerMiscUiState.Success)

                with(binding) {
                    loadingProgressBar.hideIf(uiState !is SellerMiscUiState.Loading)
                    retryButton.isVisible = uiState is SellerMiscUiState.Error
                }

                when (uiState) {
                    is SellerMiscUiState.Success -> setupSellerMiscLayout(uiState.sellerDetail)
                    is SellerMiscUiState.Error -> showShortToast(uiState.message)
                    SellerMiscUiState.Loading -> Unit
                }
            }
        }

        // Retry button
        binding.retryButton.setOnClickListener {
            viewModel.onFetchSellerMisc(navArgs.sellerId)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Attach map fragment
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container, SupportMapFragment.newInstance())
            .commitNow()

        // Set map night mode
        viewLifecycleOwner.lifecycleScope.launch {
            if (requireContext().isNightModeOn()) {
                val nightStyle = MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.night_map_style
                )
                getMapInstance().setMapStyle(nightStyle)
            }
        }

        // Add seller coordinate
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sellerLocation.collect { location ->
                location?.let {
                    getMapInstance().run {
                        val latLng = LatLng(it.latitude, it.longitude)
                        addMarker { position(latLng) }
                        moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM_LEVEL))
                    }
                }
            }
        }
    }

    private fun setupSellerMiscLayout(sellerDetail: SellerDetail) = binding.run {
        titleTextView.text = sellerDetail.getNormalizedName()

        ratingBar.rating = sellerDetail.orderRating.toFloat()
        ratingTextView.text = sellerDetail.getNormalizedOrderRating()

        phoneNumTextView.text = sellerDetail.phoneNum

        websiteTextView.text = sellerDetail.website
        websiteTextView.isGone = sellerDetail.website.isNullOrBlank()

        addressTextView.text = sellerDetail.getNormalizedAddress()
        openingHoursTextView.text = sellerDetail.openingHours

        descriptionSubtitleTextView.isGone = sellerDetail.description.isNullOrBlank()
        descriptionTextView.text = sellerDetail.getNormalizedDescription()
        descriptionTextView.isGone = sellerDetail.description.isNullOrBlank()
    }

    private suspend fun getMapInstance(): GoogleMap {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container)
            as SupportMapFragment
        return mapFragment.awaitMap()
    }
}