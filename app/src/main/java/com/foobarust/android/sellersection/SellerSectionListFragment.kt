package com.foobarust.android.sellersection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
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
class SellerSectionListFragment : FullScreenDialogFragment(),
    SellerSectionsAdapter.SellerSectionsAdapterListener {

    private var binding: FragmentSellerSectionListBinding by AutoClearedValue(this)
    private val viewModel: SellerSectionListViewModel by viewModels()
    private val navArgs: SellerSectionListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onFetchSellerSections(navArgs.sellerId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerSectionListBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.collapsingToolbarLayout, null)

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sellerDetail.collect { sellerDetail ->
                sellerDetail?.let {
                    binding.sellerImageView.bindGlideUrl(
                        imageUrl = it.imageUrl,
                        centerCrop = true,
                        placeholder = R.drawable.placeholder_card
                    )

                    binding.collapsingToolbarLayout.title = getString(
                        R.string.seller_section_list_toolbar_title,
                        sellerDetail.getNormalizedName()
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

        return binding.root
    }

    override fun onSellerSectionItemClicked(sectionBasic: SellerSectionBasic) {
        findNavController(R.id.sellerSectionListFragment)?.navigate(
            SellerSectionListFragmentDirections.actionSellerSectionListFragmentToSellerSectionFragment(
                sectionId = sectionBasic.id
            )
        )
    }

    override fun onSellerSectionItemLongClicked(
        view: View,
        sectionBasic: SellerSectionBasic
    ): Boolean {
        return true
    }
}