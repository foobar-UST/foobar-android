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
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerFragment : Fragment() {

    private var binding: FragmentSellerBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val sellerViewModel: SellerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerBinding.inflate(inflater, container, false)

        // Setup view pager
        val sellerPagerAdapter = SellerPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle,
            sellerPages = sellerViewModel.sellerPages
        )

        binding.sellerViewPager.run {
            adapter = sellerPagerAdapter
            // Set page limit to prevent scrolling lag
            offscreenPageLimit = 2

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    sellerViewModel.currentTabPage = position
                }
            })
        }

        TabLayoutMediator(binding.sellerTabLayout, binding.sellerViewPager) { tab, position ->
            tab.text = sellerViewModel.sellerPages[position].title
        }.attach()

        // Navigate to seller detail
        sellerViewModel.navigateToSellerDetail.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerFragment)?.navigate(
                SellerFragmentDirections.actionSellerFragmentToSellerDetailFragment(property = it)
            )
        }

        // Navigate to seller action
        sellerViewModel.navigateToSellerAction.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerFragment)?.navigate(
                SellerFragmentDirections.actionSellerFragmentToSellerActionDialog()
            )
        }

        // Navigate to suggest item
        sellerViewModel.navigateToSuggestItem.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerFragment)?.navigate(
                SellerFragmentDirections.actionSellerFragmentToSellerItemDetailFragment(it)
            )
        }

        // Navigate to seller section detail
        sellerViewModel.navigateToSellerSection.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerFragment)?.navigate(
                SellerFragmentDirections.actionSellerFragmentToSellerSectionFragment(property = it)
            )
        }

        // Observe bottom navigation scroll to top, and propagate to view pager
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.scrollToTop.collect { currentGraph ->
                if (currentGraph == R.id.sellerFragment) {
                    sellerViewModel.onScrollToTop()
                }
            }
        }

        // Navigate to onboarding tutorial
        mainViewModel.navigateToOnboardingTutorial.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerFragment)?.navigate(
                SellerFragmentDirections.actionSellerFragmentToTutorialFragment()
            )
        }

        return binding.root
    }
}