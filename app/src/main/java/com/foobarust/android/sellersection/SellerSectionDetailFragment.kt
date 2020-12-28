package com.foobarust.android.sellersection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.databinding.FragmentSellerSectionDetailBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.scrollToTopWhenFirstItemInserted
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Created by kevin on 12/22/20
 */

@AndroidEntryPoint
class SellerSectionDetailFragment : Fragment(),
    SellerSectionDetailAdapter.SellerSectionDetailAdapterListener,
    ParticipantsAdapter.ParticipantsAdapterListener,
    MoreSectionsAdapter.MoreSectionsAdapterListener {

    private var binding: FragmentSellerSectionDetailBinding by AutoClearedValue(this)
    private val navArgs: SellerSectionDetailFragmentArgs by navArgs()
    private val viewModel: SellerSectionDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onFetchSectionDetail(
            sellerId = navArgs.sellerId,
            sectionId = navArgs.sectionId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerSectionDetailBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerSectionDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val sectionDetailAdapter = SellerSectionDetailAdapter(this)

        binding.recyclerView.run {
            adapter = sectionDetailAdapter
            setHasFixedSize(true)
        }

        viewModel.sectionDetailListModels.observe(viewLifecycleOwner) {
            sectionDetailAdapter.submitList(it)
        }

        // Scroll top for deferred item
        viewLifecycleOwner.lifecycleScope.launch {
            sectionDetailAdapter.scrollToTopWhenFirstItemInserted(binding.recyclerView)
        }

        // Show toast message
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            viewModel.onFetchSectionDetail(
                sellerId = navArgs.sellerId,
                sectionId = navArgs.sectionId
            )
        }

        return binding.root
    }

    override fun onSellerInfoItemClicked(sellerId: String) {

    }

    override fun onParticipantItemClicked(userId: String) {

    }

    override fun onParticipantsShowMoreClicked(sectionId: String) {

    }

    override fun onSectionClicked(sectionId: String) {

    }

    override fun onSectionsShowMoreClicked(sellerId: String) {

    }
}