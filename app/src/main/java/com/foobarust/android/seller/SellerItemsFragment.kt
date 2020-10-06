package com.foobarust.android.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.common.PagingLoadStateAdapter
import com.foobarust.android.databinding.FragmentSellerItemsBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.anyError
import com.foobarust.android.utils.parentViewModels
import com.foobarust.android.utils.showShortToast
import com.foobarust.domain.models.SellerItemBasic
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Receive category data
        val categoryId = requireArguments().getString(ARG_CATEGORY_ID) ?:
            throw IllegalArgumentException("Category id not found.")

        sellerItemsViewModel.onReceiveCategory(categoryId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellerItemsBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerItemsFragment.sellerItemsViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val sellerItemsAdapter = SellerItemsAdapter(this)

        binding.itemsRecyclerView.run {
            adapter = sellerItemsAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { sellerItemsAdapter.retry() }
            )
        }

        // Submit paging data to adapter
        viewLifecycleOwner.lifecycleScope.launch {
            sellerItemsViewModel.sellerItems.collectLatest {
                sellerItemsAdapter.submitData(it)
            }
        }

        // Control views corresponding to load states
        sellerItemsAdapter.addLoadStateListener { loadStates ->
            sellerItemsViewModel.onLoadStateChanged(loadStates.source.refresh)

            loadStates.anyError()?.let {
                sellerDetailViewModel.showMessage(it.error.message)
            }
        }

        return binding.root
    }

    override fun onSellerItemClicked(sellerItemBasic: SellerItemBasic) {
        showShortToast("item: ${sellerItemBasic.id}")
    }

    companion object {
        const val ARG_CATEGORY_ID = "arg_category_id"

        @JvmStatic
        fun newInstance(categoryId: String): SellerItemsFragment {
            return SellerItemsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY_ID, categoryId)
                }
            }
        }
    }
}