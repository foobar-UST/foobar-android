package com.foobarust.android.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentExploreBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExploreFragment : Fragment(), ExploreAdapter.ExploreAdapterListener {

    private var binding: FragmentExploreBinding by AutoClearedValue(this)
    private val viewModel: ExploreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false)

        // Setup recycler view
        val itemCategoryAdapter = ExploreAdapter(this)

        with(binding.exploreRecyclerView) {
            adapter = itemCategoryAdapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.exploreListModels.collect {
                itemCategoryAdapter.submitList(it)
            }
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.exploreUiState.collect {
                with(binding) {
                    loadingProgressBar.isVisible = it is ExploreUiState.Loading
                    loadErrorLayout.loadErrorLayout.isVisible = it is ExploreUiState.Error
                }
                
                if (it is ExploreUiState.Error) {
                    showShortToast(it.message)
                }
            }
        }

        // Error layout
        binding.loadErrorLayout.retryButton.setOnClickListener {
            viewModel.onRefreshExploreList()
        }

        // Navigate to seller list
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToSellerList.collect {
                findNavController(R.id.exploreFragment)?.navigate(
                    ExploreFragmentDirections.actionExploreFragmentToSellerListFragment(it)
                )
            }
        }

        return binding.root
    }

    override fun onItemCategoryClicked(categoryId: String) {
        viewModel.onNavigateToSellerList(categoryId)
    }
}