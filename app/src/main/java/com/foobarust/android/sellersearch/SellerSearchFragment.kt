package com.foobarust.android.sellersearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class SellerSearchFragment : DialogFragment(),
    SellerSearchAdapter.SellerSearchAdapterListener {

    private var binding: FragmentSellerSearchBinding by AutoClearedValue(this)
    private val viewModel: SellerSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight_Search)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerSearchBinding.inflate(inflater, container, false).apply {
            root.applyLayoutFullscreen()

            with(searchEditText) {
                applySystemWindowInsetsMargin(applyTop = true)
                requestFocus()
            }

            clearTextButton.applySystemWindowInsetsMargin(applyTop = true)
        }

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

        return binding.root
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