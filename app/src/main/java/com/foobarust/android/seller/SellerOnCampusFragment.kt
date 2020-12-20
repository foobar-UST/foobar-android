package com.foobarust.android.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.foobarust.android.R
import com.foobarust.android.common.PagingLoadStateAdapter
import com.foobarust.android.databinding.FragmentSellerOnCampusBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.promotion.PromotionAdapter
import com.foobarust.android.promotion.PromotionAdvertiseAdapter
import com.foobarust.android.promotion.PromotionSuggestAdapter
import com.foobarust.android.utils.*
import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.promotion.SuggestBasic
import com.foobarust.domain.models.seller.SellerBasic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/11/20
 */

@AndroidEntryPoint
class SellerOnCampusFragment : Fragment(),
    PromotionAdvertiseAdapter.PromotionAdvertiseAdapterListener,
    PromotionSuggestAdapter.PromotionSuggestAdapterListener,
    SellerOnCampusAdapter.SellerOnCampusAdapterListener {

    private var binding: FragmentSellerOnCampusBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val sellerViewModel: SellerViewModel by parentViewModels()
    private val sellerOnCampusViewModel: SellerOnCampusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerOnCampusBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerOnCampusFragment.sellerOnCampusViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup on campus list
        val concatAdapter = ConcatAdapter()
        val promotionAdapter = PromotionAdapter(
            lifecycle = viewLifecycleOwner.lifecycle,
            advertiseAdapterListener = this,
            suggestAdapterListener = this
        )
        val sellerAdapter = SellerOnCampusAdapter(this)

        concatAdapter.addAdapter(promotionAdapter)
        concatAdapter.addAdapter(sellerAdapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { sellerAdapter.retry() }
        ))

        binding.recyclerView.run {
            adapter = concatAdapter
            setHasFixedSize(true)
        }

        // Fixed the issue when the promotion banner is inserted after the suggestion list,
        // and got hidden at the top of the recycler view
        scrollToTopWhenNewItemsInserted(promotionAdapter)

        // Subscribe for promotion items
        sellerOnCampusViewModel.promotionModelItems.observe(viewLifecycleOwner) {
            promotionAdapter.submitList(it)
        }

        // Subscribe for seller items
        viewLifecycleOwner.lifecycleScope.launch {
            sellerOnCampusViewModel.sellerListModels.collectLatest {
                sellerAdapter.submitData(it)
                //binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        // Retry button
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sellerOnCampusViewModel.onReloadPromotion()
            sellerAdapter.retry()
        }

        // Control views corresponding to load states
        sellerAdapter.addLoadStateListener { loadStates ->
            sellerOnCampusViewModel.onLoadStateChanged(loadStates.source.refresh)

            loadStates.anyError()?.let {
                showShortToast(it.error.message)
            }
        }

        /*
        // Swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            sellerOnCampusViewModel.reloadPromotionItems()
            sellerAdapter.refresh()
            scrollToTopWhenNewItemsInserted(promotionAdapter)
        }
         */

        // Scroll to top when the tab is reselected
        mainViewModel.scrollToTop.observe(viewLifecycleOwner) {
            binding.recyclerView.scrollToTop()
        }

        return binding.root
    }

    override fun onPromotionAdvertiseItemClicked(advertiseBasic: AdvertiseBasic) {
        if (!requireActivity().launchCustomTab(advertiseBasic.url)) {
            showShortToast(getString(R.string.error_resolve_activity_failed))
        }
    }

    override fun onSellerListItemClicked(sellerBasic: SellerBasic) {
        sellerViewModel.onNavigateToSellerDetail(sellerBasic)
    }

    override fun onSellerListItemLongClicked(view: View, sellerBasic: SellerBasic): Boolean {
        sellerViewModel.onNavigateToSellerAction()
        return true
    }

    override fun onPromotionSuggestItemClicked(suggestBasic: SuggestBasic) {
        sellerViewModel.onNavigateToSuggestItem(suggestBasic)
    }

    private fun scrollToTopWhenNewItemsInserted(promotionAdapter: PromotionAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            promotionAdapter.firstItemInsertedScrollTop(binding.recyclerView)
        }
    }
}
