package com.foobarust.android.sellersection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerSectionDetailBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 12/22/20
 */

@AndroidEntryPoint
class SellerSectionDetailFragment : Fragment(),
    SellerSectionDetailAdapter.SellerSectionDetailAdapterListener,
    ParticipantsAdapter.ParticipantsAdapterListener,
    RelatedSectionsAdapter.RelatedSectionsAdapterListener {

    private var binding: FragmentSellerSectionDetailBinding by AutoClearedValue(this)
    private val sectionViewModel: SellerSectionViewModel by navGraphViewModels(R.id.navigation_seller_section)
    private val sectionDetailViewModel: SellerSectionDetailViewModel by viewModels()
    private val navArgs: SellerSectionDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            sectionDetailViewModel.onFetchSectionDetail(navArgs.sectionId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerSectionDetailBinding.inflate(inflater, container, false)

        // Setup recycler view
        val sectionDetailAdapter = SellerSectionDetailAdapter(this)

        with(binding.sectionDetailRecyclerView) {
            adapter = sectionDetailAdapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sectionDetailViewModel.sellerSectionDetailListModels.collectLatest {
                sectionDetailAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sectionDetailViewModel.sectionDetailUiState.collect { uiState ->
                with(binding) {
                    loadingProgressBar.isVisible = uiState is SellerSectionDetailUiState.Loading
                    loadErrorLayout.root.isVisible = uiState is SellerSectionDetailUiState.Error
                }

                if (uiState is SellerSectionDetailUiState.Error) {
                    showShortToast(uiState.message)
                }
            }
        }

        // Show menu button
        viewLifecycleOwner.lifecycleScope.launch {
            sectionDetailViewModel.showOpenMenuButton.collect { isShow ->
                binding.openMenuButtonLayout.isVisible = isShow
            }
        }

        // Add to cart button
        binding.openMenuButton.setOnClickListener {
            sectionDetailViewModel.onNavigateToSellerDetail()
        }

        // Swipe refresh layout
        binding.swipeRefreshLayout.setOnRefreshListener {
            sectionDetailViewModel.onFetchSectionDetail(
                sectionId = navArgs.sectionId,
                isSwipeRefresh = true
            )
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sectionDetailViewModel.onFetchSectionDetail(
                sectionId = navArgs.sectionId,
                isSwipeRefresh = true
            )
        }

        // Set toolbar title
        sectionDetailViewModel.toolbarTitle.observe(viewLifecycleOwner) {
            sectionViewModel.onUpdateToolbarTitle(it)
        }

        // Navigate to seller detail
        viewLifecycleOwner.lifecycleScope.launch {
            sectionDetailViewModel.navigateToSellerDetail.collect {
                sectionViewModel.onNavigateToSellerDetail(it)
            }
        }

        // Navigate to section related
        viewLifecycleOwner.lifecycleScope.launch {
            sectionDetailViewModel.navigateToSectionRelated.collect {
                findNavController(R.id.sellerSectionDetailFragment)?.navigate(
                    SellerSectionDetailFragmentDirections
                        .actionSellerSectionDetailFragmentToSellerSectionRelatedFragment(it)
                )
            }
        }

        // Finish swipe refreshing
        viewLifecycleOwner.lifecycleScope.launch {
            sectionDetailViewModel.finishSwipeRefresh.collect {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        // Show toast
        viewLifecycleOwner.lifecycleScope.launch {
            sectionDetailViewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        return binding.root
    }

    override fun onSellerInfoItemClicked(sellerId: String) {
        // Show ship from location map route
        sectionViewModel.onNavigateToSellerMisc(sellerId)
    }

    override fun onParticipantItemClicked(userId: String) {

    }

    override fun onParticipantsExpandClicked(sectionId: String) {
        findNavController().navigate(
            SellerSectionDetailFragmentDirections
                .actionSellerSectionDetailFragmentToSellerSectionParticipantsFragment()
        )
    }

    override fun onRelatedSectionClicked(sectionId: String) {
        sectionViewModel.onNavigateToSellerSection(sectionId)
    }

    override fun onExpandRelatedSections(sellerId: String) {
        sectionDetailViewModel.onNavigateToSectionRelated()
    }
}