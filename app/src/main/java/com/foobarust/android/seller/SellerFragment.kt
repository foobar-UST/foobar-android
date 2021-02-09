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
        val sellerPages: List<SellerPage> = listOf(
            SellerPage(
                tag = SellerOnCampusFragment.TAG,
                title = requireContext().getString(R.string.seller_tab_on_campus),
                fragment = { SellerOnCampusFragment() }
            ),
            SellerPage(
                tag = SellerOffCampusFragment.TAG,
                title = requireContext().getString(R.string.seller_tab_off_campus),
                fragment = { SellerOffCampusFragment() }
            )
        )

        sellerPagerAdapter = SellerPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = viewLifecycleOwner.lifecycle,
            sellerPages = sellerPages
        )

        binding.sellerViewPager.run {
            adapter = sellerPagerAdapter
            offscreenPageLimit = 1

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    sellerViewModel.onCurrentPageChanged(
                        tag = sellerPages[position].tag
                    )
                }
            })
        }

        // Setup tab layout
        TabLayoutMediator(binding.sellerTabLayout, binding.sellerViewPager) { tab, position ->
            tab.text = sellerPages[position].title
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

        // Launch promotion custom tab
        sellerViewModel.navigateToPromotionDetail.observe(viewLifecycleOwner) {
            if (!CustomTabHelper.launchCustomTab(
                    context = requireContext(),
                    url = it,
                    colorInt = requireContext().themeColor(R.attr.colorPrimarySurface)
                )) {
                showShortToast(getString(R.string.error_resolve_activity_failed))
            }
        }
        // Navigate to tutorial
        mainViewModel.navigateToTutorial.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerFragment)?.navigate(
                SellerFragmentDirections.actionSellerFragmentToTutorialFragment()
            )
        }

        // Navigate to cart timeout dialog
        mainViewModel.navigateToCartTimeout.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerFragment)?.navigate(
                SellerFragmentDirections.actionSellerFragmentToCartTimeoutDialog(cartItemsCount = it)
            )
        }

        return binding.root
    }
}