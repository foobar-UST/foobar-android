package com.foobarust.android.sellerdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.R
import com.foobarust.android.common.PagingLoadStateAdapter
import com.foobarust.android.databinding.FragmentSellerItemsBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.anyError
import com.foobarust.android.utils.parentViewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/4/20
 */

@AndroidEntryPoint
class SellerItemsFragment : Fragment(), SellerItemsAdapter.SellerItemsAdapterListener {

    private var binding: FragmentSellerItemsBinding by AutoClearedValue(this)
    private val sellerDetailViewModel: SellerDetailViewModel by parentViewModels()
    private val sellerItemsViewModel: SellerItemsViewModel by viewModels()

    private val property: SellerItemsProperty by lazy {
        requireArguments().getParcelable<SellerItemsProperty>(ARG_PROPERTY) ?:
            throw IllegalArgumentException("SellerItemsProperty not found.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sellerItemsViewModel.onFetchItemsForCategory(property)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerItemsBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerItemsFragment.sellerItemsViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val sellerItemsAdapter = SellerItemsAdapter(this)
        binding.recyclerView.run {
            adapter = sellerItemsAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { sellerItemsAdapter.retry() }
            )
        }

        // Submit paging data to adapter
        viewLifecycleOwner.lifecycleScope.launch {
            sellerItemsViewModel.itemsListModels.collectLatest {
                sellerItemsAdapter.submitData(it)
            }
        }

        // Control views corresponding to load states
        sellerItemsAdapter.addLoadStateListener { loadStates ->
            sellerItemsViewModel.onLoadStateChanged(loadStates.source.refresh)
            loadStates.anyError()?.let {
                sellerDetailViewModel.showToastMessage(it.error.message)
            }
        }

        // Setup recyclerview bottom padding correspond to cart bottom bar
        sellerDetailViewModel.showCartBottomBar.observe(viewLifecycleOwner) { show ->
            val bottomPadding = if (show) {
                requireContext().resources.getDimension(R.dimen.cart_bottom_bar_height)
            } else {
                0.0
            }
            binding.recyclerView.updatePadding(bottom = bottomPadding.toInt())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fix crashing when using animateLayoutChanges on parent layout
        // See: https://stackoverflow.com/questions/60004140/pages-contain-a-viewgroup-with-a-layouttransition-or-animatelayoutchanges-tr
        binding.itemsLayout.layoutTransition.setAnimateParentHierarchy(false)
    }

    override fun onSellerItemClicked(itemId: String) {
        sellerDetailViewModel.onNavigateToSellerItemDetail(
            sellerId = property.sellerId,
            itemId = itemId
        )
    }

    companion object {
        const val ARG_PROPERTY = "arg_property"

        @JvmStatic
        fun newInstance(property: SellerItemsProperty): SellerItemsFragment {
            return SellerItemsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROPERTY, property)
                }
            }
        }
    }
}