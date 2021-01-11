package com.foobarust.android.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.foobarust.android.R
import com.foobarust.android.common.PagingLoadStateAdapter
import com.foobarust.android.databinding.FragmentSellerOffCampusBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.promotion.PromotionAdapter
import com.foobarust.android.promotion.PromotionAdvertiseAdapter
import com.foobarust.android.promotion.PromotionSuggestAdapter
import com.foobarust.android.sellersection.SellerSectionsAdapter
import com.foobarust.android.utils.*
import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.promotion.SuggestBasic
import com.foobarust.domain.models.seller.SellerSectionBasic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/11/20
 */

@AndroidEntryPoint
class SellerOffCampusFragment : Fragment(),
    PromotionAdvertiseAdapter.PromotionAdvertiseAdapterListener,
    PromotionSuggestAdapter.PromotionSuggestAdapterListener,
    SellerSectionsAdapter.SellerOffCampusAdapterListener {

    private var binding: FragmentSellerOffCampusBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val sellerViewModel: SellerViewModel by parentViewModels()
    private val sellerOffCampusViewModel: SellerOffCampusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerOffCampusBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerOffCampusFragment.sellerOffCampusViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup off-campus list
        val concatAdapter = ConcatAdapter()
        val promotionAdapter = PromotionAdapter(
            lifecycle = viewLifecycleOwner.lifecycle,
            advertiseAdapterListener = this,
            suggestAdapterListener = this
        )
        val sectionsAdapter = SellerSectionsAdapter(this)

        concatAdapter.addAdapter(promotionAdapter)
        concatAdapter.addAdapter(sectionsAdapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { sectionsAdapter.retry() }
        ))

        binding.recyclerView.run {
            adapter = concatAdapter
            setHasFixedSize(true)
        }

        normalizeListPosition(promotionAdapter)

        // Submit promotion items
        sellerOffCampusViewModel.promotionListModels.observe(viewLifecycleOwner) {
            promotionAdapter.submitList(it)
        }


        // Submit section items
        viewLifecycleOwner.lifecycleScope.launch {
            sellerOffCampusViewModel.sectionsListModels.collectLatest {
                sectionsAdapter.submitData(it)
            }
        }

        // Retry button
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sectionsAdapter.refresh()
            sellerOffCampusViewModel.onReloadPromotion()
        }

        // Control views corresponding to load states
        sectionsAdapter.addLoadStateListener { loadStates ->
            sellerOffCampusViewModel.onPagingLoadStateChanged(loadStates.source.refresh)
            loadStates.anyError()?.let {
                showShortToast(it.error.message)
            }
        }

        // Swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            sellerOffCampusViewModel.onSwipeRefresh()
            //sellerOffCampusViewModel.onReloadPromotion()
            sectionsAdapter.refresh()
            normalizeListPosition(promotionAdapter)
        }

        // Scroll to top when the tab is reselected
        viewLifecycleOwner.lifecycleScope.launch {
            sellerViewModel.scrollToTop.collect { pagePosition ->
                if (pagePosition == 1) {
                    binding.recyclerView.smoothScrollToTop()
                }
            }
        }

        // Setup recyclerview bottom padding correspond to cart bottom bar
        mainViewModel.showCartBottomBar.observe(viewLifecycleOwner) { show ->
            val bottomPadding = if (show) {
                requireContext().resources.getDimension(R.dimen.cart_bottom_bar_height)
            } else {
                0.0
            }
            binding.recyclerView.updatePadding(bottom = bottomPadding.toInt())
        }

        // Show toast
        sellerOffCampusViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Swipe refresh layout
        sellerOffCampusViewModel.isSwipeRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
            //binding.swipeRefreshLayout.isEnabled = !isRefreshing
        }

        return binding.root
    }

    override fun onSellerSectionItemClicked(sectionBasic: SellerSectionBasic) {
        sellerViewModel.onNavigateToSellerSection(sectionBasic)
    }

    override fun onSellerSectionItemLongClicked(view: View, sectionBasic: SellerSectionBasic): Boolean {
        return true
    }

    override fun onPromotionAdvertiseItemClicked(advertiseBasic: AdvertiseBasic) {
        mainViewModel.onLaunchCustomTab(url = advertiseBasic.url)
    }

    override fun onPromotionSuggestItemClicked(suggestBasic: SuggestBasic) {
        sellerViewModel.onNavigateToSuggestItem(suggestBasic)
    }

    private fun normalizeListPosition(promotionAdapter: PromotionAdapter) {
        // Fixed the issue when the promotion banner is inserted after the suggestion list,
        // and got hidden at the top of the recycler view
        viewLifecycleOwner.lifecycleScope.launch {
            promotionAdapter.scrollToTopWhenFirstItemInserted(binding.recyclerView)
        }
    }
}