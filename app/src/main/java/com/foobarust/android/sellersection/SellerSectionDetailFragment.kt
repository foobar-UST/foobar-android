package com.foobarust.android.sellersection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerSectionDetailBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.scrollToTopWhenFirstItemInserted
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 12/22/20
 */

@AndroidEntryPoint
class SellerSectionDetailFragment : Fragment(),
    SellerSectionDetailAdapter.SellerSectionDetailAdapterListener,
    SectionDetailParticipantsAdapter.SectionDetailParticipantsAdapterListener,
    SectionDetailMoreSectionsAdapter.SectionDetailMoreSectionsAdapterListener {

    private var binding: FragmentSellerSectionDetailBinding by AutoClearedValue(this)
    private val sectionViewModel: SellerSectionViewModel by navGraphViewModels(R.id.navigation_seller_section)
    private val sectionDetailViewModel: SellerSectionDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            sectionViewModel.sectionDetail.collect { sectionDetail ->
                sectionDetail?.let {
                    sectionDetailViewModel.onReceiveSellerDetail(sectionDetail = it)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerSectionDetailBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerSectionDetailFragment.sectionDetailViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val sectionDetailAdapter = SellerSectionDetailAdapter(this)

        binding.recyclerView.run {
            adapter = sectionDetailAdapter
            setHasFixedSize(true)
        }

        sectionDetailViewModel.sectionDetailListModels.observe(viewLifecycleOwner) {
            sectionDetailAdapter.submitList(it)
        }

        // Scroll top for deferred item
        viewLifecycleOwner.lifecycleScope.launch {
            sectionDetailAdapter.scrollToTopWhenFirstItemInserted(binding.recyclerView)
        }

        // Show toast message
        sectionDetailViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Add to cart button
        binding.addItemsButton.setOnClickListener {
            sectionViewModel.onNavigateToSellerDetail()
        }

        return binding.root
    }

    override fun onSellerInfoItemClicked(sellerId: String) {
        sectionViewModel.onNavigateToSellerDetail()
    }

    override fun onParticipantItemClicked(userId: String) {

    }

    override fun onParticipantsShowMoreClicked(sectionId: String) {
        findNavController().navigate(
            SellerSectionDetailFragmentDirections
                .actionSellerSectionDetailFragmentToSellerSectionParticipantsFragment()
        )
    }

    override fun onSectionClicked(sectionId: String) {
        sectionViewModel.onNavigateToSellerSection(sectionId)
    }

    override fun onSectionsShowMoreClicked(sellerId: String) {
        findNavController().navigate(
            SellerSectionDetailFragmentDirections
                .actionSellerSectionDetailFragmentToSellerSectionMoreSectionsFragment()
        )
    }
}