package com.foobarust.android.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.databinding.FragmentOrderRecentBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.parentViewModels
import com.foobarust.android.utils.showShortToast
import com.foobarust.android.utils.smoothScrollToTop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 1/29/21
 */

@AndroidEntryPoint
class OrderRecentFragment : Fragment(), OrderRecentAdapter.OrderRecentAdapterListener {

    private var binding: FragmentOrderRecentBinding by AutoClearedValue(this)
    private val orderViewModel: OrderViewModel by parentViewModels()
    private val orderRecentViewModel: OrderRecentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderRecentBinding.inflate(inflater, container, false)

        // Setup recycler view
        val recentAdapter = OrderRecentAdapter(this)

        binding.orderItemsRecyclerView.run {
            adapter = recentAdapter
            setHasFixedSize(true)
        }


        // Setup swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            orderRecentViewModel.onFetchOrderItems(true)
        }

        // List models
        viewLifecycleOwner.lifecycleScope.launch {
            orderRecentViewModel.orderRecentListModels.collect {
                recentAdapter.submitList(it)
            }
        }


        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            orderRecentViewModel.orderRecentUiState.collect { uiState ->
                binding.loadingProgressBar.isVisible = uiState is OrderRecentUiState.Loading
                if (uiState is OrderRecentUiState.Error) {
                    showShortToast(uiState.message)
                }
            }
        }

        // Scroll to top when the tab is reselected
        viewLifecycleOwner.lifecycleScope.launch {
            orderViewModel.scrollToTop.collect { pagePosition ->
                if (pagePosition == 0) {
                    binding.orderItemsRecyclerView.smoothScrollToTop()
                }
            }
        }

        // Finish swipe to refresh
        viewLifecycleOwner.lifecycleScope.launch {
            orderRecentViewModel.finishSwipeRefresh.collect {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        return binding.root
    }

    override fun onActiveOrderClicked(orderId: String) {
        orderViewModel.onNavigateToOrderDetail(orderId)
    }
}