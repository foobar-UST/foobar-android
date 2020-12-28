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
import com.foobarust.android.common.PagingLoadStateAdapter
import com.foobarust.android.databinding.FragmentSellerOffCampusBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.SellerSectionBasic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/11/20
 */

@AndroidEntryPoint
class SellerOffCampusFragment : Fragment(), SellerOffCampusAdapter.SellerOffCampusAdapterListener {

    private var binding: FragmentSellerOffCampusBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val sellerViewModel: SellerViewModel by parentViewModels()
    private val sellerOffCampusViewModel: SellerOffCampusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerOffCampusBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerOffCampusFragment.sellerOffCampusViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup off-campus list
        val concatAdapter = ConcatAdapter()
        val sellerOffCampusAdapter = SellerOffCampusAdapter(this)

        concatAdapter.addAdapter(sellerOffCampusAdapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { sellerOffCampusAdapter.retry() }
        ))

        binding.recyclerView.run {
            adapter = concatAdapter
            setHasFixedSize(true)
        }

        // Submit section items
        viewLifecycleOwner.lifecycleScope.launch {
            sellerOffCampusViewModel.offCampusListModels.collectLatest {
                sellerOffCampusAdapter.submitData(it)
            }
        }

        // Retry button
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sellerOffCampusAdapter.retry()
        }

        // Control views corresponding to load states
        sellerOffCampusAdapter.addLoadStateListener { loadStates ->
            sellerOffCampusViewModel.onLoadStateChanged(loadStates.source.refresh)
            loadStates.anyError()?.let {
                showShortToast(it.error.message)
            }
        }

        // Scroll to top when the tab is reselected
        mainViewModel.scrollToTop.observe(viewLifecycleOwner) {
            binding.recyclerView.smoothScrollToTop()
        }

        // Setup recyclerview bottom padding correspond to cart bottom bar
        mainViewModel.showCartBottomBar.observe(viewLifecycleOwner) { show ->
            val bottomPadding = if (show) {
                requireContext().resources.getDimension(R.dimen.cart_bottom_bar_height)
            } else {
                0.0
            }
            binding.recyclerView.updatePadding(bottom = bottomPadding.toInt())
        }

        return binding.root
    }

    override fun onSellerSectionItemClicked(sectionBasic: SellerSectionBasic) {
        sellerViewModel.onNavigateToSellerSection(sectionBasic)
    }

    override fun onSellerSectionItemLongClicked(view: View, sectionBasic: SellerSectionBasic): Boolean {
        // TODO: onSellerSectionItemLongClicked
        return true
    }
}