package com.foobarust.android.order

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
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentOrderDetailBinding
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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
            viewModel = this@OrderDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Hide bottom sheet at start
        binding.bottomSheet.isGone = true

        // Setup order detail list
        val detailAdapter = OrderDetailAdapter(this)

        binding.orderDetailRecyclerView.run {
            adapter = detailAdapter
            setHasFixedSize(true)
        }

        viewModel.orderDetailListModels.observe(viewLifecycleOwner) {
            detailAdapter.submitList(it)
        }

        viewModel.orderDetailUiState.observe(viewLifecycleOwner) {
            if (it is OrderDetailUiState.Error) {
                showShortToast(it.message)
            }
        }

        // Show map fragment for selected order states
        viewModel.bottomSheetFullScreen.observe(viewLifecycleOwner) { fullScreen ->
            with(binding) {
                mapContainer.isVisible = fullScreen == false
                binding.bottomSheet.isVisible = true
            }

            if (fullScreen == true) {
                setupBottomSheetFullScreen()
                updateToolbarColor(requireContext().themeColor(R.attr.colorSurface))
            } else if (fullScreen == false) {
                setupMapFragment()
                setupBottomSheetCollapsed()
            }
        }

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            viewModel.onFetchOrderDetail(navArgs.orderId)
        }

        // Navigate to seller misc
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToSellerMisc.collect {
                findNavController(R.id.orderDetailFragment)?.navigate(
                    OrderDetailFragmentDirections.actionOrderDetailFragmentToSellerMiscFragment(it)
                )
            }
        }

        return binding.root
    }

    override fun onNavigateToSellerContact() {
        viewModel.onNavigateToSellerMisc()
    }

    private fun setupMapFragment() {
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
    }

    private fun setupBottomSheetFullScreen() {
        attachBottomSheetBehavior()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false
    }

    private fun setupBottomSheetCollapsed() {
        attachBottomSheetBehavior()

        // Update toolbar based on bottom sheet state
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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
            binding.orderDetailRecyclerView.layoutCompletedFlow().collect { itemsShown ->
                if (itemsShown <= 0) return@collect
                val lastStateItemPos = viewModel.getLastOrderStateItemPosition()
                val lastStateItemView = binding.orderDetailRecyclerView
                    .findViewHolderForAdapterPosition(lastStateItemPos)
                    ?.itemView
                if (lastStateItemPos > 0 && lastStateItemView != null) {
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

    private fun getSupportMapFragment(): SupportMapFragment? {
        return childFragmentManager.findFragmentById(R.id.map_container) as? SupportMapFragment
    }
}