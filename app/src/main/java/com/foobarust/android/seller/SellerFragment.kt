package com.foobarust.android.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerFragment : Fragment() {

    private var binding: FragmentSellerBinding by AutoClearedValue(this)
    private val viewModel: SellerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerBinding.inflate(inflater, container, false)

        // Setup on-campus and off-campus tabs
        viewModel.sellerPages.observe(viewLifecycleOwner) {
            val sellerPagerAdapter = SellerPagerAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = lifecycle,
                sellerPages = it
            )

            binding.sellerViewPager.run {
                adapter = sellerPagerAdapter
                isUserInputEnabled = false
            }

            TabLayoutMediator(binding.sellerTabLayout, binding.sellerViewPager) { tab, position ->
                tab.text = it[position].title
            }.attach()
        }

        // Navigate to seller detail
        viewModel.navigateToSellerDetail.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerFragment)?.navigate(
                SellerFragmentDirections.actionSellerFragmentToSellerDetailFragment(
                    sellerId = it
                )
            )
        }

        // Navigate to seller action
        viewModel.navigateToSellerAction.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerFragment)?.navigate(
                SellerFragmentDirections.actionSellerFragmentToSellerActionDialog()
            )
        }

        // Navigate to suggest item
        viewModel.navigateToSuggestItem.observe(viewLifecycleOwner) {
            findNavController(R.id.sellerFragment)?.navigate(
                SellerFragmentDirections.actionSellerFragmentToSellerItemDetailFragment(it)
            )
        }

        return binding.root
    }
}