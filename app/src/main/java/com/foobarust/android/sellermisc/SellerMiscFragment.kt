package com.foobarust.android.sellermisc

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

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Setup bottom sheet
        viewLifecycleOwner.lifecycleScope.launch {
            binding.bottomSheet.bottomSheetPeekUntil(
                toView = binding.headerGroup
            )
        }

        binding.bottomSheet.bottomSheetApplyDefaultBackground()

        // Show bottom sheet when loading is success
        viewModel.uiState.observe(viewLifecycleOwner) {
            binding.bottomSheet.bottomSheetHideIf(it !is UiState.Success)
        }

        // Show toast
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup map view
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment

        // Add seller coordinate
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
}