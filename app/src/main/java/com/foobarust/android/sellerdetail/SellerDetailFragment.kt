package com.foobarust.android.sellerdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.NavigationSellerDirections
import com.foobarust.android.R
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentSellerDetailBinding
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.getNormalizedTitle
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
    private val sellerDetailViewModel: SellerDetailViewModel by viewModels()
    private val args: SellerDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Receive seller id argument
        sellerDetailViewModel.onFetchSellerDetailWithCatalogs(sellerId = args.sellerId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerDetailBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerDetailFragment.sellerDetailViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Remove listener on CollapsingToolbarLayout, so that toolbar top padding can work properly
        // Issue: https://github.com/material-components/material-components-android/issues/1310
        ViewCompat.setOnApplyWindowInsetsListener(binding.collapsingToolbarLayout, null)

        // Set up tab layout and view pager when seller detail is successfully fetched
        sellerDetailViewModel.sellerDetailWithCatalogs.observe(viewLifecycleOwner) { sellerDetailWithCatalogs ->
            sellerDetailWithCatalogs?.let {
                val catalogPagerAdapter = SellerCatalogPagerAdapter(
                    fragmentManager = childFragmentManager,
                    lifecycle = viewLifecycleOwner.lifecycle,
                    sellerId = args.sellerId,
                    sellerCatalogs = it.catalogs
                )

                binding.itemsViewPager.adapter = catalogPagerAdapter

                TabLayoutMediator(binding.categoryTabLayout, binding.itemsViewPager) { tab, position ->
                    tab.text = it.catalogs[position].getNormalizedTitle()
                }.attach()
            }
        }

        // Show toast message
        sellerDetailViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Load at most two consecutive pages for view pager at once
        binding.itemsViewPager.offscreenPageLimit = 2

        // Show toolbar title only when collapsed
        viewLifecycleOwner.lifecycleScope.launch {
            binding.appBarLayout.doOnOffsetChanged().collect {
                sellerDetailViewModel.onShowToolbarTitleChanged(
                    isShow = it == AppBarStateChangedListener.State.COLLAPSED
                )
            }
        }

        // Navigation back arrow button
        binding.toolbar.setNavigationOnClickListener { dismiss() }

        // Navigate to SellerMiscFragment
        binding.miscButton.setOnClickListener {
            sellerDetailViewModel.onShowSellerMisc()
        }

        sellerDetailViewModel.navigateToSellerMisc.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerDetailFragment)?.navigate(
                SellerDetailFragmentDirections.actionSellerDetailFragmentToSellerMiscFragment(
                    sellerId = args.sellerId
                )
            )
        }

        // Navigate to ItemDetailDialog
        sellerDetailViewModel.navigateToItemDetail.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerDetailFragment)?.navigate(
                SellerDetailFragmentDirections.actionSellerDetailFragmentToSellerItemDetailFragment(it)
            )
        }

        // Setup action chips
        sellerDetailViewModel.detailActions.observe(viewLifecycleOwner) { detailActions ->
            val chips = detailActions.map { action ->
                Chip(requireContext(), null, R.attr.sellerActionChipStyle).apply {
                    hide()
                    text = action.title
                    chipIconTint = requireContext().getColorStateListFrom(action.colorRes)
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

        // SnackBar
        sellerDetailViewModel.showSnackBarMessage.observe(viewLifecycleOwner) {
            showMessageSnackBar(message = it)
        }

        binding.cartBottomBar.cartBottomBarCardView.setOnClickListener {
            findNavController(R.id.sellerDetailFragment)?.navigate(
                NavigationSellerDirections.actionGlobalCheckoutFragment()
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

    private fun showMessageSnackBar(message: String) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }
}