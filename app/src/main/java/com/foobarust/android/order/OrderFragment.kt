package com.foobarust.android.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

@AndroidEntryPoint
class OrderFragment : Fragment() {

    private var binding: FragmentOrderBinding by AutoClearedValue(this)
    private var orderPagerAdapter: OrderPagerAdapter by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: OrderViewModel by viewModels()

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

        return binding.root
    }
}