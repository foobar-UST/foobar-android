package com.foobarust.android.sellermisc

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerMiscBinding
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolyline
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/11/20
 */

private const val MAP_ZOOM_LEVEL = 15f
private const val MAP_ROUTE_WIDTH = 10f

@AndroidEntryPoint
class SellerMiscFragment : FullScreenDialogFragment() {

    private var binding: FragmentSellerMiscBinding by AutoClearedValue(this)
    private val viewModel: SellerMiscViewModel by viewModels()
    private val navArgs: SellerMiscFragmentArgs by navArgs()
    private var bottomSheetBehavior: BottomSheetBehavior<*> by AutoClearedValue(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onFetchSellerDetail(sellerId = navArgs.sellerId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerMiscBinding.inflate(inflater, container, false)

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
        viewModel.sellerMiscUiState.observe(viewLifecycleOwner) {
            bottomSheetBehavior.hideIf(it !is SellerMiscUiState.Success)

            with(binding) {
                loadingProgressBar.bindProgressHideIf(it !is SellerMiscUiState.Loading)
                retryButton.bindHideIf(it !is SellerMiscUiState.Error)
            }

            when (it) {
                is SellerMiscUiState.Success -> setupSellerMiscLayout(it.sellerDetail)
                is SellerMiscUiState.Error -> showShortToast(it.message)
                SellerMiscUiState.Loading -> Unit
            }
        }

        // Retry button
        binding.retryButton.setOnClickListener {
            viewModel.onFetchSellerDetail(navArgs.sellerId)
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
                getSupportMapFragment()?.awaitMap()?.run {
                    val nightStyle = MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.night_map_style
                    )
                    setMapStyle(nightStyle)
                }
            }
        }

        // Add seller coordinate
        viewModel.sellerLocation.observe(viewLifecycleOwner) { latLng ->
            viewLifecycleOwner.lifecycleScope.launch {
                getSupportMapFragment()?.awaitMap()?.run {
                    addMarker { position(latLng) }
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM_LEVEL))
                }
            }
        }

        // Add seller route
        viewModel.offCampusDeliveryRoute.observe(viewLifecycleOwner) { polyline ->
            polyline?.let {
                viewLifecycleOwner.lifecycleScope.launch {
                    getSupportMapFragment()?.awaitMap()?.run {
                        addPolyline {
                            color(requireContext().themeColor(R.attr.colorSecondary))
                            width(MAP_ROUTE_WIDTH)
                            addAll(it)
                        }
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

    private fun getSupportMapFragment(): SupportMapFragment? {
        return childFragmentManager.findFragmentById(R.id.map_container) as? SupportMapFragment
    }
}