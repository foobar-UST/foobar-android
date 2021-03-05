package com.foobarust.android.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.viewpager2.widget.ViewPager2
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentOrderBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

const val SAVED_STATE_KEY_RATING_COMPLETED = "rating_completed"

@AndroidEntryPoint
class OrderFragment : Fragment() {

    private var binding: FragmentOrderBinding by AutoClearedValue(this)
    private var orderPagerAdapter: OrderPagerAdapter by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: OrderViewModel by viewModels()

    private var ratingResultObserver: LifecycleEventObserver? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)

        orderPagerAdapter = OrderPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = viewLifecycleOwner.lifecycle,
            orderPages = viewModel.orderPages
        )

        binding.orderViewPager.run {
            adapter = orderPagerAdapter
            offscreenPageLimit = 2

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.currentTabPage = position
                }
            })
        }

        // Setup tab layout
        TabLayoutMediator(binding.orderTabLayout, binding.orderViewPager) { tab, position ->
            tab.text = viewModel.orderPages[position].title
        }.attach()

        // Observe bottom navigation scroll to top, and propagate to view pager
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.scrollToTop.collect { currentGraph ->
                if (currentGraph == R.id.orderFragment) {
                    viewModel.onScrollToTop()
                }
            }
        }

        // Navigate to OrderDetailFragment
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToOrderDetail.collect {
                findNavController(R.id.orderFragment)?.navigate(
                    OrderFragmentDirections.actionOrderFragmentToOrderDetailFragment(orderId = it)
                )
            }
        }

        // Navigate to rating
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToRating.collect {
                findNavController(R.id.orderFragment)?.navigate(
                    OrderFragmentDirections.actionOrderFragmentToRatingFragment(orderId = it)
                )
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Attach rating result observer
        val navBackStackEntry = getNavBackStackEntry() ?: return

        if (ratingResultObserver == null) {
            ratingResultObserver = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME &&
                    navBackStackEntry.savedStateHandle.contains(SAVED_STATE_KEY_RATING_COMPLETED)
                ) {
                    val savedStateHandle = navBackStackEntry.savedStateHandle
                    val result = savedStateHandle.get<Boolean>(SAVED_STATE_KEY_RATING_COMPLETED)
                        ?: return@LifecycleEventObserver

                    if (result) {
                        viewModel.onRefreshHistoryList()
                        savedStateHandle.remove<Boolean>(SAVED_STATE_KEY_RATING_COMPLETED)
                    }
                }
            }.also {
                navBackStackEntry.lifecycle.addObserver(it)
            }
        } else {
            navBackStackEntry.lifecycle.addObserver(ratingResultObserver!!)
        }

        // Detach observer when fragment is destroyed
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                ratingResultObserver?.let {
                    navBackStackEntry.lifecycle.removeObserver(it)
                }
            }
        })
    }

    private fun getNavBackStackEntry(): NavBackStackEntry? {
        return findNavController(R.id.orderFragment)?.getBackStackEntry(R.id.orderFragment)
    }
}