package com.foobarust.android.sellersection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerSectionListBinding
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.shared.PagingLoadStateAdapter
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.SellerSectionBasic
import com.foobarust.domain.models.seller.getNormalizedName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 2/23/21
 */

@AndroidEntryPoint
class SellerSectionListFragment : FullScreenDialogFragment(R.layout.fragment_seller_section_list),
    SellerSectionsAdapter.SellerSectionsAdapterListener {

    private val binding: FragmentSellerSectionListBinding by viewBinding(FragmentSellerSectionListBinding::bind)
    private val viewModel: SellerSectionListViewModel by viewModels()
    private val navArgs: SellerSectionListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onFetchSellerSections(navArgs.sellerId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutFullscreen(aboveNavBar = true)

        binding.appBarLayout.applySystemWindowInsetsPadding(applyTop = true)

        // Navigation back arrow button
        binding.toolbar.setNavigationOnClickListener {
            findNavController(R.id.sellerSectionListFragment)?.navigateUp()
        }

        // Setup recycler view
        val sectionsAdapter = SellerSectionsAdapter(this)

        binding.sectionsRecyclerView.run {
            adapter = sectionsAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { sectionsAdapter.retry() }
            )
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sectionsListModels.collectLatest {
                sectionsAdapter.submitData(it)
            }
        }

        // Seller detail
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sellerDetail.collect { sellerDetail ->
                sellerDetail?.let {
                    binding.toolbar.title = getString(
                        R.string.seller_section_list_toolbar_title,
                        it.getNormalizedName()
                    )
                }
            }
        }

        // Retry button
        binding.loadErrorLayout.retryButton.setOnClickListener {
            viewModel.onFetchSellerSections(navArgs.sellerId)
        }

        // Control views corresponding to load states
        sectionsAdapter.addLoadStateListener { loadStates ->
            with(loadStates) {
                updateViews(
                    mainLayout = binding.sectionsRecyclerView,
                    errorLayout = binding.loadErrorLayout.loadErrorLayout,
                    progressBar = binding.loadingProgressBar
                )
                anyError()?.let {
                    showShortToast(it.toString())
                }
            }
        }
    }

    override fun onSellerSectionItemClicked(sectionBasic: SellerSectionBasic) {
        findNavController(R.id.sellerSectionListFragment)?.navigate(
            SellerSectionListFragmentDirections.actionSellerSectionListFragmentToSellerSectionFragment(
                sectionId = sectionBasic.id
            )
        )
    }
}