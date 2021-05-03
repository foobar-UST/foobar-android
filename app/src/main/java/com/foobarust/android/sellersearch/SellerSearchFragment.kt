package com.foobarust.android.sellersearch

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerSearchBinding
import com.foobarust.android.sellerdetail.SellerDetailProperty
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.SellerType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerSearchFragment : DialogFragment(R.layout.fragment_seller_search),
    SellerSearchAdapter.SellerSearchAdapterListener {

    private val binding: FragmentSellerSearchBinding by viewBinding(FragmentSellerSearchBinding::bind)
    private val viewModel: SellerSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight_Search)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutFullscreen(aboveNavBar = true)

        binding.clearTextButton.applySystemWindowInsetsMargin(applyTop = true)

        binding.loadingProgressBar.setVisibilityAfterHide(View.INVISIBLE)

        // Request edit text focus to show keyboard
        with(binding.searchEditText) {
            applySystemWindowInsetsMargin(applyTop = true)
            requestFocus()
        }

        // Search results recycler view
        val sellerSearchAdapter = SellerSearchAdapter(this)

        binding.searchRecyclerView.run {
            adapter = sellerSearchAdapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchListModels.collect {
                sellerSearchAdapter.submitList(it)
            }
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchUiState.collect { uiState ->
                binding.loadingProgressBar.hideIf(uiState !is SellerSearchUiState.Loading)
                if (uiState is SellerSearchUiState.Error) {
                    showShortToast(uiState.message)
                }
            }
        }

        // Dismiss when clicking scrim region
        viewLifecycleOwner.lifecycleScope.launch {
            binding.searchRecyclerView.touchOutsideItemsFlow().collect {
                findNavController(R.id.sellerSearchFragment)?.navigateUp()
            }
        }

        // Submit search query
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onUpdateSearchQuery(text.toString())
        }

        // Clear text
        binding.clearTextButton.setOnClickListener {
            with(binding.searchEditText.text) {
                if (isEmpty()) {
                    findNavController(R.id.sellerSearchFragment)?.navigateUp()
                } else {
                    clear()
                }
            }
        }
    }

    override fun onSellerItemClicked(sellerId: String, sellerType: SellerType) {
        val directions =  if (sellerType == SellerType.ON_CAMPUS) {
            SellerSearchFragmentDirections.actionSellerSearchFragmentToSellerDetailFragment(
                SellerDetailProperty(sellerId = sellerId)
            )
        } else {
            SellerSearchFragmentDirections.actionSellerSearchFragmentToSellerSectionListFragment(
                sellerId = sellerId
            )
        }

        findNavController(R.id.sellerSearchFragment)?.navigate(directions)
    }
}