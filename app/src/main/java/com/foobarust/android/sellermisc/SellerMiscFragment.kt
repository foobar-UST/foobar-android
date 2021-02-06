package com.foobarust.android.sellermisc

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.common.UiState
import com.foobarust.android.databinding.FragmentSellerMiscBinding
import com.foobarust.android.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
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

@AndroidEntryPoint
class SellerMiscFragment : FullScreenDialogFragment() {

    private var binding: FragmentSellerMiscBinding by AutoClearedValue(this)
    private val viewModel: SellerMiscViewModel by viewModels()
    private val args: SellerMiscFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onFetchSellerDetail(sellerId = args.sellerId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerMiscBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerMiscFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setupBottomSheet()

        setupMapFragment()

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }


        // Show toast
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        return binding.root
    }

    private fun setupMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment

        // Add coordinate
        viewModel.latLng.observe(viewLifecycleOwner) { latLng ->
            viewLifecycleOwner.lifecycleScope.launch {
                mapFragment.awaitMap().run {
                    addMarker { position(latLng) }
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }

        // Add route
        viewModel.polyline.observe(viewLifecycleOwner) { polyline ->
            polyline?.let {
                viewLifecycleOwner.lifecycleScope.launch {
                    mapFragment.awaitMap().run {
                        addPolyline {
                            color(requireContext().themeColor(R.attr.colorSecondary))
                            width(10f)
                            addAll(it)
                        }
                    }
                }
            }
        }
    }

    private fun setupBottomSheet() {
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)

        with(binding.bottomSheet) {
            background = MaterialShapeDrawable(
                context,
                null,
                R.attr.bottomSheetStyle,
                0
            ).apply {
                fillColor = ColorStateList.valueOf(
                    context.themeColor(R.attr.colorSurface)
                )
                elevation = resources.getDimension(R.dimen.elevation_xmedium)

                initializeElevationOverlay(context)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            bottomSheetPeekTo(
                bottomSheet = binding.bottomSheet,
                toView = binding.headerGroup
            )
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
            behavior.hideIf(it !is UiState.Success)
        }
    }
}