package com.foobarust.android.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerListBinding
import com.foobarust.android.sellerdetail.SellerDetailProperty
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.shared.PagingLoadStateAdapter
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.SellerType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 2/27/21
 */

@AndroidEntryPoint
class SellerListFragment : FullScreenDialogFragment(), SellersAdapter.SellersAdapterListener {
    
    private var binding: FragmentSellerListBinding by AutoClearedValue(this)
    private val viewModel: SellerListViewModel by viewModels()
    private val navArgs: SellerListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onFetchCategorySellers(navArgs.property)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerListBinding.inflate(inflater, container, false)

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController(R.id.sellerListFragment)?.navigateUp()
        }

        // Setup recycler view
        val sellersAdapter = SellersAdapter(this)

        with(binding.sellersRecyclerView) {
            adapter = sellersAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { sellersAdapter.retry() }
            )
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sellersListModels.collectLatest {
                sellersAdapter.submitData(it)
            }
        }

        // Observe view model property
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sellerListProperty.collect { property ->
                binding.toolbar.title = property?.categoryTitle
            }
        }

        // Ui State
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sellerListFetchUiState.collect {
                with(binding) {
                    loadingProgressBar.isVisible = it is SellerListFetchUiState.Loading
                    loadErrorLayout.root.isVisible = it is SellerListFetchUiState.Error
                }

                if (it is SellerListFetchUiState.Error) {
                    showShortToast(it.message)
                }
            }
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            if (viewModel.sellerListFetchUiState.value is SellerListFetchUiState.Error) {
                viewModel.onFetchCategorySellers(navArgs.property)
            } else {
                sellersAdapter.refresh()
            }
        }

        // Control views with respect to load states
        sellersAdapter.addLoadStateListener { loadStates ->
            with(loadStates) {
                updateViews(
                    mainLayout = binding.sellersRecyclerView,
                    errorLayout = binding.loadErrorLayout.loadErrorLayout,
                    progressBar = binding.loadingProgressBar
                )
                anyError()?.let {
                    showShortToast(it.toString())
                }
            }
        }

        return binding.root
    }

    override fun onSellerClicked(sellerId: String, sellerType: SellerType) {
        when (sellerType) {
            SellerType.ON_CAMPUS -> findNavController(R.id.sellerListFragment)?.navigate(
                SellerListFragmentDirections.actionSellerListFragmentToSellerDetailFragment(
                    SellerDetailProperty(sellerId = sellerId)
                )
            )
            SellerType.OFF_CAMPUS -> findNavController(R.id.sellerListFragment)?.navigate(
                SellerListFragmentDirections.actionSellerListFragmentToSellerSectionListFragment(
                    sellerId
                )
            )
        }
    }
}