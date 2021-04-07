package com.foobarust.android.orderdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentOrderDetailBinding
import com.foobarust.android.shared.AppConfig.MAP_ROUTE_WIDTH
import com.foobarust.android.shared.AppConfig.MAP_ZOOM_LEVEL
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolyline
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 1/28/21
 */

@AndroidEntryPoint
class OrderDetailFragment : FullScreenDialogFragment(),
    OrderDetailAdapter.OrderDetailAdapterListener {

    private var binding: FragmentOrderDetailBinding by AutoClearedValue(this)
    private val viewModel: OrderDetailViewModel by viewModels()
    private val navArgs: OrderDetailFragmentArgs by navArgs()
    private var bottomSheetBehavior: BottomSheetBehavior<*> by AutoClearedValue(this)

    private var sellerMarker: Marker? = null
    private var delivererMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onFetchOrderDetail(navArgs.orderId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false).apply {
            root.applyLayoutFullscreen()
            toolbar.applySystemWindowInsetsPadding(applyTop = true)
            loadingProgressBar.setVisibilityAfterHide(View.GONE)
            bottomSheet.isGone = true         // Hide bottom sheet at start
        }

        // Setup order detail list
        val detailAdapter = OrderDetailAdapter(this)

        with(binding.orderDetailRecyclerView) {
            adapter = detailAdapter
            layoutManager = NotifyingLinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
            )
            setHasFixedSize(true)
        }

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            viewModel.onFetchOrderDetail(navArgs.orderId)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.orderDetailListModels.collectLatest {
                detailAdapter.submitList(it)
            }
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
             viewModel.orderDetailUiState.collect { uiState ->
                 with(binding) {
                     loadingProgressBar.hideIf(uiState !is OrderDetailUiState.Loading)
                     loadErrorLayout.root.isVisible = uiState is OrderDetailUiState.Error
                     mapContainer.isGone = uiState is OrderDetailUiState.Error
                 }

                 if (uiState is OrderDetailUiState.Error) {
                     showShortToast(uiState.message)
                 }
             }
        }

        // Show map fragment for selected order states
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showMap.collect { showMap ->
                with(binding) {
                    mapContainer.isVisible = showMap
                    bottomSheet.isVisible = !showMap
                }

                if (showMap) {
                    initializeMapFragment()
                    setupBottomSheetCollapsed()
                } else {
                    setupBottomSheetFullScreen()
                    updateToolbarColor(requireContext().themeColor(R.attr.colorSurface))
                }
            }
        }

        return binding.root
    }

    override fun onNavigateToSellerMisc() {
        val orderDetail = viewModel.orderDetail.value ?: return
        findNavController(R.id.orderDetailFragment)?.navigate(
            OrderDetailFragmentDirections.actionOrderDetailFragmentToSellerMiscFragment(
                orderDetail.sellerId
            )
        )
    }

    override fun onPickupVerifyOrder() {
        viewModel.orderDetail.value?.let { orderDetail ->
            findNavController(R.id.orderDetailFragment)?.navigate(
                OrderDetailFragmentDirections.actionOrderDetailFragmentToVerifyOrderFragment(
                    orderId = orderDetail.id,
                    verifyCode = orderDetail.verifyCode
                )
            )
        }
    }

    private fun initializeMapFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container, SupportMapFragment.newInstance())
            .commitNow()

        // Initialize map
        viewLifecycleOwner.lifecycleScope.launch {
            val mapInstance = getMapInstance()

            // Set night mode
            if (requireContext().isNightModeOn()) {
                val nightStyle = MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.night_map_style
                )
                mapInstance.setMapStyle(nightStyle)
            }

            // Attach marker listener
            mapInstance.setOnMarkerClickListener { clickedMarker ->
                when (clickedMarker) {
                    sellerMarker -> onNavigateToSellerMisc()
                    delivererMarker -> navigateToDelivererInfo()
                }
                true
            }
        }

        // Add seller marker (on-campus)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deliveryLocation.collect {
                getMapInstance().run {
                    val latLng = LatLng(it.latitude, it.longitude)
                    sellerMarker?.remove()
                    sellerMarker = addMarker { position(latLng) }
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM_LEVEL))
                }
            }
        }

        // Add deliverer marker (off-campus)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.delivererMarkerInfo.collect { marker ->
                marker?.let {
                    delivererMarker?.remove()
                    delivererMarker = getMapInstance().loadMarker(
                        context = requireContext(),
                        latLng = LatLng(
                            it.locationPoint.latitude,
                            it.locationPoint.longitude
                        ),
                        imageUrl = it.userDelivery.photoUrl,
                        placeholder = R.drawable.ic_user
                    )
                }
            }
        }

        // Zoom to deliverer
        viewLifecycleOwner.lifecycleScope.launch {
            val location = viewModel.delivererMarkerInfo.firstNotNull().locationPoint
            getMapInstance().moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    MAP_ZOOM_LEVEL
                )
            )
        }

        // Add deliverer route
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.delivererRoute.collect { route ->
                if (route.isNotEmpty()) {
                    getMapInstance().addPolyline {
                        color(requireContext().themeColor(R.attr.colorSecondary))
                        width(MAP_ROUTE_WIDTH)
                        addAll(route.map { LatLng(it.latitude, it.longitude) })
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        sellerMarker = null
        delivererMarker = null
        super.onDestroyView()
    }

    private fun setupBottomSheetFullScreen() {
        attachBottomSheetBehavior()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false
    }

    private fun setupBottomSheetCollapsed() {
        attachBottomSheetBehavior()

        // Update toolbar based on bottom sheet state
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // TODO: fix toolbar flicker when changing color
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    updateToolbarColor(requireContext().themeColor(R.attr.colorSurface))
                } else {
                    updateToolbarColor(requireContext().getColorCompat(android.R.color.transparent))
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })

        // Set bottom sheet peek height to show the last state item
        viewLifecycleOwner.lifecycleScope.launch {
            val notifyingLinearLayoutManager = binding.orderDetailRecyclerView.layoutManager
                as NotifyingLinearLayoutManager

            notifyingLinearLayoutManager.getVisibleItemsCount().collect { visibleItemsCount ->
                if (visibleItemsCount <= 0) return@collect

                val lastStateItemPosition = viewModel.getLastOrderStateItemPosition()
                val lastStateItemView = binding.orderDetailRecyclerView
                    .findViewHolderForAdapterPosition(lastStateItemPosition)?.itemView
                if (lastStateItemPosition > 0 && lastStateItemView != null) {
                    bottomSheetBehavior.setPeekHeight(lastStateItemView.bottom, true)
                }
            }
        }
    }

    private fun attachBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior<FrameLayout>()

        // Set bottom sheet behavior
        binding.bottomSheet.run {
            (layoutParams as CoordinatorLayout.LayoutParams).behavior = bottomSheetBehavior
            requestLayout()
        }
    }

    private fun updateToolbarColor(@ColorInt color: Int) {
        binding.toolbar.setBackgroundColor(color)
    }

    private suspend fun getMapInstance(): GoogleMap {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container)
            as SupportMapFragment
        return mapFragment.awaitMap()
    }

    private fun navigateToDelivererInfo() {
        val delivererId = viewModel.delivererProfile.value?.id ?: return
        findNavController(R.id.orderDetailFragment)?.navigate(
            OrderDetailFragmentDirections.actionOrderDetailFragmentToDelivererInfoFragment(delivererId)
        )
    }
}