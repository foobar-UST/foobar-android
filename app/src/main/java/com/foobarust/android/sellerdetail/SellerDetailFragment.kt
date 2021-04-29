package com.foobarust.android.sellerdetail

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerDetailBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.seller.SellerListProperty
import com.foobarust.android.selleritem.SellerItemDetailProperty
import com.foobarust.android.sellerrating.SellerRatingDetailProperty
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.*
import com.foobarust.domain.models.cart.hasItems
import com.foobarust.domain.models.seller.*
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by kevin on 9/24/20
 */

@AndroidEntryPoint
class SellerDetailFragment : FullScreenDialogFragment() {

    private var binding: FragmentSellerDetailBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val sellerDetailViewModel: SellerDetailViewModel by viewModels()
    private val navArgs: SellerDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            sellerDetailViewModel.onFetchSellerDetail(navArgs.property)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setLayoutFullscreen()

        binding = FragmentSellerDetailBinding.inflate(inflater, container, false).apply {
            toolbar.applySystemWindowInsetsPadding(applyTop = true)
        }

        // Remove listener on CollapsingToolbarLayout, so that toolbar top padding can work properly
        // Issue: https://github.com/material-components/material-components-android/issues/1310
        ViewCompat.setOnApplyWindowInsetsListener(binding.collapsingToolbarLayout, null)

        // Navigation back arrow button
        binding.toolbar.setNavigationOnClickListener {
            findNavController(R.id.sellerDetailFragment)?.navigateUp()
        }

        // Navigate to seller misc
        binding.showMiscButton.setOnClickListener {
            findNavController(R.id.sellerDetailFragment)?.navigate(
                SellerDetailFragmentDirections.actionSellerDetailFragmentToSellerMiscFragment(
                    sellerId = navArgs.property.sellerId
                )
            )
        }

        // Navigate to checkout
        binding.cartBottomBar.cartBottomBarCardView.setOnClickListener {
            findNavController(R.id.sellerDetailFragment)?.navigate(
                SellerDetailFragmentDirections.actionSellerDetailFragmentToCheckoutFragment()
            )
        }

        // Swipe refresh layout
        binding.swipeRefreshLayout.setOnRefreshListener {
            sellerDetailViewModel.onFetchSellerDetail(
                property = navArgs.property,
                isSwipeRefresh = true
            )
        }

        binding.sellerInfoTextView.drawableFitVertical()
        binding.sectionInfoTextView.drawableFitVertical()

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sellerDetailViewModel.onFetchSellerDetail(navArgs.property)
        }
        
        // Setup seller detail
        viewLifecycleOwner.lifecycleScope.launch { 
            sellerDetailViewModel.sellerDetail.collect { sellerDetail ->
                if (sellerDetail == null) return@collect
                
                with(binding) {
                    nameTextView.text = sellerDetail.getNormalizedName()
                    sectionInfoTextView.isVisible = sellerDetail.type == SellerType.OFF_CAMPUS
                    
                    sellerImageView.loadGlideUrl(
                        imageUrl = sellerDetail.imageUrl,
                        centerCrop = true,
                        placeholder = R.drawable.placeholder_card
                    )
                    sellerImageView.contentDescription = sellerDetail.getNormalizedName()
                }

                setupNoticeBanner(sellerDetail)
            }
        }
        
        // Setup section detail
        viewLifecycleOwner.lifecycleScope.launch {
            sellerDetailViewModel.sectionDetail.collect { sectionDetail ->
                if (sectionDetail == null) return@collect
                
                with(binding) {
                    sectionInfoTextView.text = getString(
                        R.string.seller_detail_format_section_info,
                        sectionDetail.getDeliveryTimeString(),
                        sectionDetail.getDeliveryDateString()
                    )
                }
            }
        }
        
        // Setup seller info
        viewLifecycleOwner.lifecycleScope.launch { 
            sellerDetailViewModel.sellerInfo.collect { 
                binding.sellerInfoTextView.text = it
            }
        }

        // Set up seller catalogs
        viewLifecycleOwner.lifecycleScope.launch {
            sellerDetailViewModel.sellerCatalogs.collect {
                setupCatalogsViewPager(it)
            }
        }

        // Enable swipe refresh layout
        viewLifecycleOwner.lifecycleScope.launch {
            sellerDetailViewModel.enableSwipeRefresh.collect {
                binding.swipeRefreshLayout.run {
                    isRefreshing = false
                    isEnabled = it
                }
            }
        }

        // Get toolbar scroll state
        viewLifecycleOwner.lifecycleScope.launch {
            binding.appBarLayout.state().collect {
                sellerDetailViewModel.onAppBarLayoutStateChanged(state = it)
            }
        }

        // Show toolbar title when the toolbar is collapsed
        viewLifecycleOwner.lifecycleScope.launch { 
            sellerDetailViewModel.toolbarTitle.collect { 
                binding.toolbar.title = it
            }
        }

        // Finish swipe to refresh
        viewLifecycleOwner.lifecycleScope.launch {
            sellerDetailViewModel.finishSwipeRefresh.collect {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        // Setup action chips
        viewLifecycleOwner.lifecycleScope.launch {
            sellerDetailViewModel.actions.collect { actions ->
                val chips = actions.map {
                    when (it) {
                        is SellerDetailAction.Rating -> buildRatingChip(it)
                        is SellerDetailAction.Category -> buildCategoryChip(it)
                    }
                }

                binding.actionChipGroup.replaceChips(chips)
            }
        }

        // Setup cart bottom bar
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.userCart.collect { userCart ->
                binding.cartBottomBar.root.isVisible = userCart != null && userCart.hasItems()

                if (userCart != null) {
                     with(binding.cartBottomBar) {
                        cartItemsCountTextView.text = getString(
                            R.string.cart_bottom_bar_format_items_count,
                            userCart.itemsCount
                        )
                        cartTotalPriceTextView.text = getString(
                            R.string.cart_bottom_bar_format_total_price,
                            userCart.totalCost
                        )
                    }
                }
            }
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            sellerDetailViewModel.sellerDetailUiState.collect {
                with(binding) {
                    loadErrorLayout.root.isVisible = it is SellerDetailUiState.Error
                    loadingProgressBar.isVisible = it is SellerDetailUiState.Loading
                    sellerDetailLayout.isVisible = it is SellerDetailUiState.Success
                    sellerItemsGroup.isVisible = it is SellerDetailUiState.Success
                }

                if (it is SellerDetailUiState.Error) {
                    showShortToast(it.message)
                }
            }
        }

        // Navigate to SellerItemDetail
        viewLifecycleOwner.lifecycleScope.launch {
            sellerDetailViewModel.navigateToItemDetail.collect {
                findNavController(R.id.sellerDetailFragment)?.navigate(
                    SellerDetailFragmentDirections.actionSellerDetailFragmentToSellerItemDetailFragment(
                        SellerItemDetailProperty(
                            sellerId = it.sellerId,
                            itemId = it.itemId,
                            sectionId = navArgs.property.sectionId
                        )
                    )
                )
            }
        }

        // Snackbar
        viewLifecycleOwner.lifecycleScope.launch {
            sellerDetailViewModel.snackBarMessage.collect {
                showMessageSnackBar(it)
            }
        }

        return binding.root
    }

    private fun setupCatalogsViewPager(catalogs: List<SellerCatalog>) {
        // Setup view pager
        with(binding.itemsViewPager) {
            // Load at most two consecutive pages for view pager at once
            offscreenPageLimit = 2
            adapter = SellerCatalogsPagerAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                sellerId = navArgs.property.sellerId,
                sellerCatalogs = catalogs
            )
            // Disable swipe refresh when view pager is being scrolled
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    sellerDetailViewModel.onViewPagerStateChanged(state)
                }
            })
        }

        // Setup tab layout
        TabLayoutMediator(binding.categoryTabLayout, binding.itemsViewPager) { tab, position ->
            tab.text = catalogs[position].getNormalizedTitle()
        }.attach()
    }
    
    private fun buildRatingChip(ratingAction: SellerDetailAction.Rating): Chip {
        return Chip(
            requireContext(), null, R.attr.actionChipStyle
        ).apply {
            isVisible = false
            text = ratingAction.ratingTitle
            chipIconTint = requireContext().buildColorStateListWith(R.color.yellow)
            chipIcon = requireContext().getDrawableOrNull(R.drawable.ic_star)

            setOnClickListener { navigateToSellerRating() }

            // Fix chip flicker when changing typeface
            // See: https://github.com/material-components/material-components-android/issues/675
            post { isVisible = true }
        }
    }

    private fun buildCategoryChip(categoryAction: SellerDetailAction.Category): Chip {
        return Chip(
            requireContext(), null, R.attr.actionChipStyle
        ).apply {
            isVisible = false
            text = categoryAction.categoryTag.capitalize(Locale.US)

            setOnClickListener { navigateToSellerList(categoryAction.categoryTag) }

            post { isVisible = true }
        }
    }

    private fun setupNoticeBanner(sellerDetail: SellerDetail) {
        with(binding.sellerNoticeBanner) {
            root.isVisible = !sellerDetail.online || !sellerDetail.notice.isNullOrBlank()

            if (sellerDetail.online) {
                noticeTextView.text = sellerDetail.notice
                noticeTextView.background = ColorDrawable(
                    requireContext().themeColor(R.attr.colorSecondaryVariant)
                )
            } else {
                noticeTextView.text = getString(R.string.seller_detail_offline_message)
                noticeTextView.background = ColorDrawable(
                    requireContext().getColorCompat(R.color.grey_disabled)
                )
            }
        }
    }

    private fun showMessageSnackBar(message: String) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun navigateToSellerRating() {
        val sellerDetail = sellerDetailViewModel.sellerDetail.value ?: return
        findNavController(R.id.sellerDetailFragment)?.navigate(
            SellerDetailFragmentDirections.actionSellerDetailFragmentToSellerRatingDetailFragment(
                SellerRatingDetailProperty(
                    sellerId = sellerDetail.id,
                    sellerName = sellerDetail.getNormalizedName(),
                    orderRating = sellerDetail.orderRating,
                    deliveryRating = sellerDetail.deliveryRating,
                    ratingCount = sellerDetail.ratingCount
                )
            )
        )
    }

    private fun navigateToSellerList(categoryTag: String) {
        findNavController(R.id.sellerDetailFragment)?.navigate(
            SellerDetailFragmentDirections.actionSellerDetailFragmentToSellerListFragment(
                SellerListProperty(categoryTag)
            )
        )
    }
}