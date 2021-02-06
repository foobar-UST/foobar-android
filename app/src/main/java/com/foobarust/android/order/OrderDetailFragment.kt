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
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentOrderDetailBinding
import com.foobarust.android.utils.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 1/28/21
 */

@AndroidEntryPoint
class OrderDetailFragment : FullScreenDialogFragment() {

    private var binding: FragmentOrderDetailBinding by AutoClearedValue(this)
    private val viewModel: OrderDetailViewModel by viewModels()
    private val navArgs: OrderDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onFetchOrderDetail(orderId = navArgs.orderId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)

        binding.bottomSheet.isGone = true

        // Setup order detail list
        val detailAdapter = OrderDetailAdapter()

        binding.orderDetailRecyclerView.run {
            adapter = detailAdapter
            setHasFixedSize(true)
        }

        viewModel.orderDetailListModels.observe(viewLifecycleOwner) {
            detailAdapter.submitList(it)
        }

        // Show map fragment for selected order states
        viewModel.showMapFragment.observe(viewLifecycleOwner) { isShow ->
            isShow?.let { setShowMapFragment(it) }
        }

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

    private fun setShowMapFragment(isShow: Boolean) {
        if (isShow) {
            // Attach map fragment
            childFragmentManager.beginTransaction()
                .replace(R.id.map_container, SupportMapFragment.newInstance())
                .commit()

            setupBottomSheet()
        } else {
            // Remove map container and fully expand bottom sheet
            binding.mapContainer.isGone = true
            binding.bottomSheet.isVisible = true
            updateToolbarColor(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    private fun setupBottomSheet() {
        // Attach bottom sheet
        val bottomSheetBehavior = BottomSheetBehavior<FrameLayout>()
        binding.bottomSheet.run {
            (layoutParams as CoordinatorLayout.LayoutParams).behavior = bottomSheetBehavior
            requestLayout()
            isVisible = true
        }

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

    private fun updateToolbarColor(@ColorInt color: Int) {
        binding.toolbar.setBackgroundColor(color)
    }
}