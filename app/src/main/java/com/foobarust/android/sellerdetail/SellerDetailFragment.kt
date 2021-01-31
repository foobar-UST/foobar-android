package com.foobarust.android.sellerdetail

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.NavigationSellerDirections
import com.foobarust.android.R
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentSellerDetailBinding
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.*
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


/**
 * Created by kevin on 9/24/20
 */

@AndroidEntryPoint
class SellerDetailFragment : FullScreenDialogFragment() {

    private var binding: FragmentSellerDetailBinding by AutoClearedValue(this)
    private val viewModel: SellerDetailViewModel by viewModels()
    private val navArgs: SellerDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Receive seller id argument
        viewModel.onFetchSellerDetail(navArgs.property)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerDetailBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Remove listener on CollapsingToolbarLayout, so that toolbar top padding can work properly
        // Issue: https://github.com/material-components/material-components-android/issues/1310
        ViewCompat.setOnApplyWindowInsetsListener(binding.collapsingToolbarLayout, null)
        // Load at most two consecutive pages for view pager at once
        binding.itemsViewPager.offscreenPageLimit = 2

        // Navigation back arrow button
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.showMiscButton.setOnClickListener {
            viewModel.onNavigateToSellerMisc()
        }

        // Cart bottom bar
        binding.cartBottomBar.cartBottomBarCardView.setOnClickListener {
            findNavController(R.id.sellerDetailFragment)?.navigate(
                NavigationSellerDirections.actionGlobalCheckoutFragment()
            )
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            viewModel.onFetchSellerDetail(navArgs.property)
        }

        // Set up tab layout and view pager when seller detail is successfully fetched
        viewModel.sellerDetailWithCatalogs.observe(viewLifecycleOwner) { sellerDetailWithCatalogs ->
            sellerDetailWithCatalogs?.let {
                setupCatalogViewPager(it.catalogs)
                setupNoticeBanner(it.sellerDetail)
            }
        }

        // Show toast message
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Show toolbar title when collapsed
        viewLifecycleOwner.lifecycleScope.launch {
            binding.appBarLayout.state().collect { state ->
                viewModel.onToolbarScrollStateChanged(
                    isCollapsed = state == AppBarLayoutState.COLLAPSED
                )
            }
        }

        viewModel.navigateToSellerMisc.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerDetailFragment)?.navigate(
                SellerDetailFragmentDirections.actionSellerDetailFragmentToSellerMiscFragment(
                    sellerId = navArgs.property.sellerId
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
                Chip(requireContext(), null, R.attr.actionChipStyle).apply {
                    visibility = View.GONE
                    text = action.title
                    chipIconTint = requireContext().getColorStateListFrom(action.colorRes)
                    action.drawableRes?.let {
                        chipIcon = requireContext().getDrawableOrNull(it)
                    }
                    setOnClickListener { setupChipActions(action.id) }
                    // Fix chip flicker when changing typeface
                    // See: https://github.com/material-components/material-components-android/issues/675
                    post { visibility = View.VISIBLE }
                }
            }

            chips.forEach { binding.actionChipGroup.addView(it) }
        }

        // SnackBar
        viewModel.snackBarMessage.observe(viewLifecycleOwner) {
            showMessageSnackBar(message = it)
        }

        // Seller info
        viewModel.sellerInfoLine.observe(viewLifecycleOwner) {
            binding.infoTextView.text = it
        }

        return binding.root
    }

    private fun setupCatalogViewPager(catalogs: List<SellerCatalog>) {
        val catalogPagerAdapter = SellerCatalogPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = viewLifecycleOwner.lifecycle,
            sellerId = navArgs.property.sellerId,
            sellerCatalogs = catalogs
        )

        binding.itemsViewPager.adapter = catalogPagerAdapter

        TabLayoutMediator(binding.categoryTabLayout, binding.itemsViewPager) { tab, position ->
            tab.text = catalogs[position].getNormalizedTitle()
        }.attach()
    }

    private fun setupChipActions(actionId: String) {
        when {
            actionId == SELLER_DETAIL_ACTION_RATING -> showShortToast("Rating clicked.")
            actionId.contains(SELLER_DETAIL_ACTION_RATING) -> showShortToast("tag clicked.")
            else -> throw IllegalStateException("Invalid chip action id $actionId")
        }
    }

    private fun setupNoticeBanner(sellerDetail: SellerDetail) {
        with(binding.sellerNoticeBanner.noticeTextView) {
            bindGoneIf(!sellerDetail.online || sellerDetail.notice == null)

            text = if (sellerDetail.online) {
                sellerDetail.notice
            } else {
                getString(R.string.seller_detail_offline_message)
            }

            background = if (sellerDetail.online) {
                ColorDrawable(context.themeColor(R.attr.colorSecondaryVariant))
            } else {
                ColorDrawable(context.getColorCompat(R.color.grey_disabled))
            }
        }
    }

    private fun showMessageSnackBar(message: String) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }
}