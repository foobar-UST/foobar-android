package com.foobarust.android.sellerdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.NavigationSellerDirections
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerDetailBinding
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.getNormalizedTitle
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


/**
 * Created by kevin on 9/24/20
 */

@AndroidEntryPoint
class SellerDetailFragment : DialogFragment() {

    private var binding: FragmentSellerDetailBinding by AutoClearedValue(this)
    private val viewModel: SellerDetailViewModel by viewModels()
    private val args: SellerDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerDetailBinding.inflate(inflater, container, false).apply {
            // Load cached data
            sellerDetailProperty = args.sellerDetailProperty
            viewModel = this@SellerDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Remove listener on CollapsingToolbarLayout, so that toolbar top padding can work properly
        // Issue: https://github.com/material-components/material-components-android/issues/1310
        ViewCompat.setOnApplyWindowInsetsListener(binding.collapsingToolbarLayout, null)

        // Receive seller id argument
        viewModel.onFetchSellerDetail(sellerId = args.sellerDetailProperty.id)

        // Set up tab layout and view pager when seller detail is successfully fetched
        viewModel.sellerCatalogs.observe(viewLifecycleOwner) {
            val catalogPagerAdapter = SellerCatalogPagerAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                sellerId = args.sellerDetailProperty.id,
                sellerCatalogs = it
            )

            binding.itemsViewPager.adapter = catalogPagerAdapter

            TabLayoutMediator(binding.categoryTabLayout, binding.itemsViewPager) { tab, position ->
                tab.text = it[position].getNormalizedTitle()
            }.attach()
        }

        // Show toast message
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Load at most two consecutive pages for view pager
        binding.itemsViewPager.offscreenPageLimit = 1

        // Show toolbar title only when collapsed
        viewLifecycleOwner.lifecycleScope.launch {
            binding.appBarLayout.doOnOffsetChanged().collect {
                viewModel.onShowToolbarTitleChanged(
                    isShow = it == AppBarStateChangedListener.State.COLLAPSED
                )
            }
        }

        // Navigation back arrow button
        binding.toolbar.setNavigationOnClickListener { dismiss() }

        // Navigate to SellerMiscFragment
        binding.miscButton.setOnClickListener {
            viewModel.onShowSellerMisc()
        }

        viewModel.navigateToSellerMisc.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerDetailFragment)?.navigate(
                SellerDetailFragmentDirections.actionSellerDetailFragmentToSellerMiscFragment(
                    sellerMiscProperty = it
                )
            )
        }

        // Navigate to ItemDetailDialog
        viewModel.navigateToItemDetail.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerDetailFragment)?.navigate(
                SellerDetailFragmentDirections.actionSellerDetailFragmentToSellerItemDetailFragment(it)
            )
        }

        // Setup action chips
        viewModel.detailActions.observe(viewLifecycleOwner) { detailActions ->
            val chips = detailActions.map { action ->
                Chip(requireContext(), null, R.attr.sellerActionChipStyle).apply {
                    hide()

                    text = action.title
                    chipIconTint = requireContext().buildColorStateList(action.colorRes)

                    action.drawableRes?.let {
                        chipIcon = requireContext().getDrawableOrNull(it)
                    }

                    setOnClickListener { setupChipActions(action.id) }

                    // Fix chip flicker when changing typeface
                    // See: https://github.com/material-components/material-components-android/issues/675
                    post { show() }
                }
            }

            chips.forEach { binding.actionChipGroup.addView(it) }
        }

        // Cart bottom bar
        binding.cartBottomBar.cartBottomBarCardView.setOnClickListener {
            findNavController().navigate(
                NavigationSellerDirections.actionGlobalCartFragment()
            )
        }

        return binding.root
    }

    private fun setupChipActions(actionId: String) {
        when {
            actionId == SELLER_DETAIL_ACTION_RATING -> showShortToast("Rating clicked.")
            actionId.contains(SELLER_DETAIL_ACTION_RATING) -> showShortToast("tag clicked.")
            else -> throw IllegalStateException("Invalid chip action id $actionId")
        }
    }
}