package com.foobarust.android.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding = FragmentOrderRecentBinding.inflate(inflater, container, false).apply {
            viewModel = orderRecentViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val recentAdapter = OrderRecentAdapter(this)

        binding.orderItemsRecyclerView.run {
            adapter = recentAdapter
            setHasFixedSize(true)
        }

        orderRecentViewModel.recentListModels.observe(viewLifecycleOwner) {
            recentAdapter.submitList(it)
        }

        // Setup swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            orderRecentViewModel.onFetchOrderItems(true)
        }

        // Scroll to top when the tab is reselected
        viewLifecycleOwner.lifecycleScope.launch {
            orderViewModel.scrollToTop.collect { pagePosition ->
                if (pagePosition == 0) {
                    binding.orderItemsRecyclerView.smoothScrollToTop()
                }
            }
        }

        // Show toast
        orderRecentViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        orderRecentViewModel.finishSwipeRefresh.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = false
        }

        return binding.root
    }

    override fun onOrderClicked(orderId: String) {
        orderViewModel.onNavigateToOrderDetail(orderId)
    }

    override fun onOrderRated(orderId: String, rating: Double) {
        showShortToast("Rating: $rating")
    }
}