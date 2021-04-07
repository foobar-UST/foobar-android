package com.foobarust.android.sellerrating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerRatingDetailBinding
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.shared.PagingLoadStateAdapter
import com.foobarust.android.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 3/3/21
 */

@AndroidEntryPoint
class SellerRatingDetailFragment : FullScreenDialogFragment(),
    SellerRatingDetailAdapter.SellerRatingDetailAdapterListener {

    private var binding: FragmentSellerRatingDetailBinding by AutoClearedValue(this)
    private val viewModel: SellerRatingDetailViewModel by viewModels()
    private val navArgs: SellerRatingDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onFetchSellerRatings(navArgs.property)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerRatingDetailBinding.inflate(inflater, container, false)

        // Set up recycler view
        val sellerRatingDetailAdapter = SellerRatingDetailAdapter(this)

        binding.ratingDetailRecyclerView.run {
            adapter = sellerRatingDetailAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { sellerRatingDetailAdapter.retry() }
            )
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ratingDetailListModels.collectLatest {
                sellerRatingDetailAdapter.submitData(it)
            }
        }

        // Toolbar
        with(binding.toolbar) {
            title = navArgs.property.sellerName
            setNavigationOnClickListener {
                findNavController(R.id.sellerRatingDetailFragment)?.navigateUp()
            }
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sellerRatingDetailAdapter.refresh()
        }

        // Control views with respect to load states
        sellerRatingDetailAdapter.addLoadStateListener { loadStates ->
            with(loadStates) {
                updateViews(
                    mainLayout = binding.ratingDetailRecyclerView,
                    errorLayout = binding.loadErrorLayout.loadErrorLayout,
                    progressBar = binding.loadingProgressBar,
                    swipeRefreshLayout = binding.swipeRefreshLayout
                )
                anyError()?.let {
                    showShortToast(it.toString())
                }
            }
        }

        // Swipe refresh layout
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onFetchSellerRatings(navArgs.property)
        }

        return binding.root
    }

    override fun onSortRatingButtonClicked() {
        // TODO: onSortRatingButtonClicked
    }
}