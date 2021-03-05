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
import com.foobarust.android.databinding.FragmentSellerOnCampusBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.promotion.AdvertiseAdapter
import com.foobarust.android.promotion.PromotionAdapter
import com.foobarust.android.shared.PagingLoadStateAdapter
import com.foobarust.android.utils.*
import com.foobarust.domain.models.promotion.AdvertiseBasic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/11/20
 */

@AndroidEntryPoint
class SellerOnCampusFragment : Fragment(),
    AdvertiseAdapter.AdvertiseAdapterListener,
    SellersAdapter.SellersAdapterListener {

    private var binding: FragmentSellerOnCampusBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val sellerViewModel: SellerViewModel by parentViewModels()
    private val sellerOnCampusViewModel: SellerOnCampusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerOnCampusBinding.inflate(inflater, container, false)

        // Setup recycler view
        val promotionAdapter = PromotionAdapter(this)
        val sellersAdapter = SellersAdapter(this)

        val concatAdapter = ConcatAdapter(
            promotionAdapter,
            sellersAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { sellersAdapter.retry() }
            )
        )

        binding.sellersRecyclerView.run {
            adapter = concatAdapter
            setHasFixedSize(true)
        }

        // Submit promotion items
        sellerOnCampusViewModel.promotionListModels.observe(viewLifecycleOwner) {
            promotionAdapter.submitList(it)
        }

        normalizeListPosition(promotionAdapter)

        // Submit seller items
        viewLifecycleOwner.lifecycleScope.launch {
            sellerOnCampusViewModel.sellersListModels.collectLatest {
                sellersAdapter.submitData(it)
            }
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sellersAdapter.refresh()
            sellerOnCampusViewModel.onReloadPromotion()
        }

        // Control views with respect to load states
        sellersAdapter.addLoadStateListener { loadStates ->
            with(loadStates) {
                updateViews(
                    mainLayout = binding.sellersRecyclerView,
                    errorLayout = binding.loadErrorLayout.loadErrorLayout,
                    progressBar = binding.loadingProgressBar,
                    swipeRefreshLayout = binding.swipeRefreshLayout
                )
                anyError()?.let {
                    showShortToast(it.toString())
                }
            }
        }

        // Swipe refresh layout
        binding.swipeRefreshLayout.setOnRefreshListener {
            sellerOnCampusViewModel.onReloadPromotion()
            sellersAdapter.refresh()
            normalizeListPosition(promotionAdapter)
        }

        // Scroll to top when the tab is reselected
        viewLifecycleOwner.lifecycleScope.launch {
            sellerViewModel.pageScrollToTop.collect { page ->
                if (page == TAG) {
                    binding.sellersRecyclerView.smoothScrollToTop()
                }
            }
        }

        // Setup recyclerview bottom padding correspond to cart bottom bar
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.showCartBottomBar.collect { show ->
                val bottomPadding = if (show) {
                    requireContext().resources.getDimension(R.dimen.cart_bottom_bar_height)
                } else {
                    0.0
                }
                binding.sellersRecyclerView.updatePadding(bottom = bottomPadding.toInt())
            }
        }

        return binding.root
    }

    override fun onAdvertiseItemClicked(advertiseBasic: AdvertiseBasic) {
        sellerViewModel.onNavigateToPromotionDetail(advertiseBasic.url)
    }

    override fun onSellerClicked(sellerId: String) {
        sellerViewModel.onNavigateToSellerDetail(sellerId)
    }

    private fun normalizeListPosition(promotionAdapter: PromotionAdapter) {
        // Fixed the issue when the promotion banner is inserted after the suggestion list,
        // and got hidden at the top of the recycler view
        viewLifecycleOwner.lifecycleScope.launch {
            promotionAdapter.scrollToTopWhenFirstItemInserted(binding.sellersRecyclerView)
        }
    }

    companion object {
        const val TAG = "SellerOnCampusFragment"
    }
}
