package com.foobarust.android.selleritem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.databinding.FragmentSellerItemsBinding
import com.foobarust.android.sellerdetail.SellerDetailViewModel
import com.foobarust.android.shared.PagingLoadStateAdapter
import com.foobarust.android.utils.*
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
        binding = FragmentSellerItemsBinding.inflate(inflater, container, false)

        // Setup recycler view
        val sellerItemsAdapter = SellerItemsAdapter(this)

        with(binding.recyclerView) {
            adapter = sellerItemsAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { sellerItemsAdapter.retry() }
            )

            doOnLayout {
                it.applySystemWindowInsetsPadding(applyBottom = true)
            }
        }

        // Submit paging data to adapter
        viewLifecycleOwner.lifecycleScope.launch {
            sellerItemsViewModel.itemsListModels.collectLatest {
                sellerItemsAdapter.submitData(it)
            }
        }

        // Control views corresponding to load states
        sellerItemsAdapter.addLoadStateListener { loadStates ->
            with(loadStates) {
                anyError()?.let {
                    showShortToast(it.error.message)
                }
            }
        }

        // Fix crashing when using animateLayoutChanges on parent layout
        // See: https://stackoverflow.com/questions/60004140/pages-contain-a-viewgroup-with-a-layouttransition-or-animatelayoutchanges-tr
        //binding.itemsLayout.layoutTransition.setAnimateParentHierarchy(false)

        return binding.root
    }

    override fun onSellerItemClicked(itemId: String) {
        sellerDetailViewModel.onNavigateToSellerItemDetail(itemId)
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