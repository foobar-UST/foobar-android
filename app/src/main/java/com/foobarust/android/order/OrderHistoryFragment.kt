package com.foobarust.android.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.databinding.FragmentOrderHistoryBinding
import com.foobarust.android.shared.PagingLoadStateAdapter
import com.foobarust.android.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 1/29/21
 */

@AndroidEntryPoint
class OrderHistoryFragment : Fragment(), OrderHistoryAdapter.OrderHistoryAdapterListener {

    private var binding: FragmentOrderHistoryBinding by AutoClearedValue(this)
    private val orderViewModel: OrderViewModel by parentViewModels()
    private val orderHistoryViewModel: OrderHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)

        // Setup recycler view
        val historyAdapter = OrderHistoryAdapter(this)

        binding.orderItemsRecyclerView.run {
            adapter = historyAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { historyAdapter.retry() }
            )
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            orderHistoryViewModel.orderHistoryListModels.collectLatest {
                historyAdapter.submitData(it)
            }
        }

        // Control views corresponding to load states
        historyAdapter.addLoadStateListener { loadStates ->
            with(loadStates) {
                updateViews(
                    mainLayout = binding.orderItemsRecyclerView,
                    progressBar = binding.loadingProgressBar,
                    swipeRefreshLayout = binding.swipeRefreshLayout
                )
                anyError()?.let {
                    showShortToast(it.toString())
                }
            }
        }

        // Swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            historyAdapter.refresh()
        }

        // Scroll to top when the tab is reselected
        viewLifecycleOwner.lifecycleScope.launch {
            orderViewModel.scrollToTop.collect { pagePosition ->
                if (pagePosition == 0) {
                    binding.orderItemsRecyclerView.smoothScrollToTop()
                }
            }
        }

        // Refresh history list after rating
        viewLifecycleOwner.lifecycleScope.launch {
            orderViewModel.refreshHistoryList.collect {
                historyAdapter.refresh()
            }
        }

        return binding.root
    }

    override fun onArchivedOrderClicked(orderId: String) {
        orderViewModel.onNavigateToOrderDetail(orderId)
    }

    override fun onDeliveredOrderClickedClicked(orderId: String) {
        orderViewModel.onNavigateToRating(orderId)
    }
}