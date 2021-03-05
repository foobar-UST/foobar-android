package com.foobarust.android.seller

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
import com.foobarust.android.databinding.FragmentSellerBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.utils.*
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerFragment : Fragment() {

    private var binding: FragmentSellerBinding by AutoClearedValue(this)
    private var sellerPagerAdapter: SellerPagerAdapter by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val sellerViewModel: SellerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerBinding.inflate(inflater, container, false)

        // Setup view pager
        sellerPagerAdapter = SellerPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = viewLifecycleOwner.lifecycle,
            sellerPages = sellerViewModel.sellerPages
        )

        binding.sellerViewPager.run {
            adapter = sellerPagerAdapter
            offscreenPageLimit = 1

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    sellerViewModel.onCurrentPageChanged(
                        tag = sellerViewModel.sellerPages[position].tag
                    )
                }
            })
        }

        // Setup tab layout
        TabLayoutMediator(binding.sellerTabLayout, binding.sellerViewPager) { tab, position ->
            tab.text = sellerViewModel.sellerPages[position].title
        }.attach()

        // Observe bottom navigation scroll to top, and propagate to view pager
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.scrollToTop.collect { currentGraph ->
                if (currentGraph == R.id.sellerFragment) {
                    sellerViewModel.onPageScrollToTop()
                }
            }
        }

        // Navigate to seller detail
        viewLifecycleOwner.lifecycleScope.launch {
            sellerViewModel.navigateToSellerDetail.collect {
                findNavController(R.id.sellerFragment)?.navigate(
                    SellerFragmentDirections.actionSellerFragmentToSellerDetailFragment(it)
                )
            }
        }

        // Navigate to section detail
        viewLifecycleOwner.lifecycleScope.launch {
            sellerViewModel.navigateToSellerSection.collect {
                findNavController(R.id.sellerFragment)?.navigate(
                    SellerFragmentDirections.actionSellerFragmentToSellerSectionFragment(it)
                )
            }
        }

        // Launch promotion custom tab
        viewLifecycleOwner.lifecycleScope.launch {
            sellerViewModel.navigateToPromotionDetail.collect {
                val launchResult = CustomTabHelper.launchCustomTab(
                    context = requireContext(),
                    url = it,
                    tabColorInt = requireContext().themeColor(R.attr.colorPrimarySurface)
                )

                if (!launchResult) {
                    showShortToast(getString(R.string.error_resolve_activity_failed))
                }
            }
        }

        return binding.root
    }
}