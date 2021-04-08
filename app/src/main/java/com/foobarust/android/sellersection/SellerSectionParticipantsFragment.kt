package com.foobarust.android.sellersection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerSectionParticipantsBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 12/22/20
 */

@AndroidEntryPoint
class SellerSectionParticipantsFragment : Fragment() {

    private var binding: FragmentSellerSectionParticipantsBinding by AutoClearedValue(this)
    private val sectionViewModel: SellerSectionViewModel by navGraphViewModels(R.id.navigation_seller_section)
    private val participantsViewModel: SellerSectionParticipantsViewModel by viewModels()
    private val navArgs: SellerSectionParticipantsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            participantsViewModel.onFetchParticipants(navArgs.userIds.asList())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerSectionParticipantsBinding.inflate(inflater, container, false)

        val participantsAdapter = ParticipantsAdapter()

        with(binding.participantsRecyclerView) {
            adapter = participantsAdapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            participantsViewModel.participantsListModels.collect {
                participantsAdapter.submitList(it)
            }
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            participantsViewModel.participantsUiState.collect { uiState ->
                with(binding) {
                    loadingProgressBar.isVisible = uiState is SellerSectionParticipantsUiState.Loading
                    loadErrorLayout.root.isVisible = uiState is SellerSectionParticipantsUiState.Error
                }

                if (uiState is SellerSectionParticipantsUiState.Error) {
                    showShortToast(uiState.message)
                }
            }
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            participantsViewModel.onFetchParticipants(navArgs.userIds.asList())
        }

        // Observe dialog back press and navigate up
        viewLifecycleOwner.lifecycleScope.launch {
            sectionViewModel.backPressed.collect {
                findNavController(R.id.sellerSectionParticipantsFragment)?.navigateUp()
            }
        }

        return binding.root
    }
}