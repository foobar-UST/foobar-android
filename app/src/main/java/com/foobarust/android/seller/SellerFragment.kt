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
import com.foobarust.android.databinding.FragmentSellerBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.promotion.PromotionAdapter
import com.foobarust.android.promotion.PromotionAdvertiseAdapter
import com.foobarust.android.promotion.PromotionSuggestAdapter
import com.foobarust.android.utils.*
import com.foobarust.domain.models.AdvertiseBasic
import com.foobarust.domain.models.SellerBasic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerFragment : Fragment(),
    PromotionAdvertiseAdapter.PromotionAdvertiseAdapterListener,
    PromotionSuggestAdapter.PromotionSuggestAdapterListener,
    SellerAdapter.SellerAdapterListener {

    private var binding: FragmentSellerBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val sellerViewModel: SellerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellerBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerFragment.sellerViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val concatAdapter = ConcatAdapter()
        val promotionAdapter = PromotionAdapter(this)
        val sellerAdapter = SellerAdapter(this)

        concatAdapter.addAdapter(promotionAdapter)
        concatAdapter.addAdapter(sellerAdapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { sellerAdapter.retry() }
        ))

        binding.sellerRecyclerView.run {
            adapter = concatAdapter
            setHasFixedSize(true)
        }

        // Fixed the issue when the promotion banner is inserted after the suggestion list,
        // and got hidden at the top of the recycler view
        viewLifecycleOwner.lifecycleScope.launch {
            promotionAdapter.firstItemInsertedScrollTop(binding.sellerRecyclerView)
        }

        // Subscribe for promotion items
        sellerViewModel.promotionModelItems.observe(viewLifecycleOwner) {
            promotionAdapter.submitList(it)
        }

        // Subscribe for seller items
        viewLifecycleOwner.lifecycleScope.launch {
            sellerViewModel.sellerModelItems.collectLatest {
                sellerAdapter.submitData(it)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        // Retry button
        binding.networkErrorLayout.retryButton.setOnClickListener {
            sellerViewModel.reloadPromotionItems()
            sellerAdapter.retry()
        }

        // Control views corresponding to load states
        sellerAdapter.addLoadStateListener { loadStates ->
            sellerViewModel.onLoadStateChanged(loadStates.source.refresh)

            loadStates.anyError()?.let {
                showShortToast(it.error.message)
            }
        }

        // Swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            sellerViewModel.reloadPromotionItems()
            sellerAdapter.refresh()
        }

        // Scroll to top when the tab is reselected
        mainViewModel.scrollToTop.observe(viewLifecycleOwner) {
            binding.sellerRecyclerView.scrollToTop()
        }

        // Show toast
        sellerViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        return binding.root
    }

    override fun onPromotionAdvertiseItemClicked(advertiseBasic: AdvertiseBasic) {
        // TODO: onPromotionBannerItemClicked
        showShortToast("Promotion clicked.")
    }

    override fun onSellerListItemClicked(sellerBasic: SellerBasic) {
        // Navigate to SellerDetail
        findNavController(R.id.sellerFragment)?.navigate(
            SellerFragmentDirections.actionSellerFragmentToSellerDetailFragment(
                sellerId = sellerBasic.id
            )
        )
    }

    override fun onSellerListItemLongClicked(view: View, sellerBasic: SellerBasic): Boolean {
        // TODO: onSellerItemLongClicked
        return true
    }

    override fun onPromotionSuggestItemClicked(itemId: String?) {
        showShortToast("suggest item: $itemId")
    }
}