package com.foobarust.android.selleritem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerItemDetailBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.sellerdetail.*
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.SellerItemBasic
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/12/20
 */

@AndroidEntryPoint
class SellerItemDetailFragment : FullScreenDialogFragment(),
    SellerItemDetailAdapter.SellerItemDetailAdapterListener {

    private var binding: FragmentSellerItemDetailBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val itemDetailViewModel: SellerItemDetailViewModel by viewModels()
    private val navArgs: SellerItemDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            itemDetailViewModel.onFetchItemDetail(navArgs.property)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerItemDetailBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerItemDetailFragment.itemDetailViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val itemDetailAdapter = SellerItemDetailAdapter(this)

        binding.recyclerView.run {
            adapter = itemDetailAdapter
            setHasFixedSize(true)
        }

        itemDetailViewModel.sellerItemDetailListModels.observe(viewLifecycleOwner) {
            itemDetailAdapter.submitList(it)
        }

        itemDetailViewModel.sellerItemDetailUiState.observe(viewLifecycleOwner) {
            if (it is SellerItemDetailUiState.Error) {
                showShortToast(it.message)
            }
        }

        itemDetailViewModel.sellerItemDetailUpdateState.observe(viewLifecycleOwner) { updateState ->
            updateState?.let {
                when (it) {
                    SellerItemDetailUiState.Success -> {
                        findNavController(R.id.sellerItemDetailFragment)?.navigateUp()
                    }
                    is SellerItemDetailUiState.Error -> {
                        showErrorMessageDialog(message = it.message)
                    }
                    SellerItemDetailUiState.Loading -> Unit
                }
            }
        }

        // Remove listener on CollapsingToolbarLayout, so that toolbar top padding can work properly
        ViewCompat.setOnApplyWindowInsetsListener(binding.collapsingToolbarLayout, null)

        // Show toolbar title only when collapsed
        viewLifecycleOwner.lifecycleScope.launch {
            binding.appBarLayout.state().collect {
                itemDetailViewModel.onToolbarScrollStateChanged(it)
            }
        }

        // Setup amount widget
        binding.amountIncrementButton.setOnClickListener {
            itemDetailViewModel.onAmountIncrement()
        }

        binding.amountDecrementButton.setOnClickListener {
            itemDetailViewModel.onAmountDecrement()
        }

        // Toolbar navigate back
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Setup submit button
        binding.submitButton.setOnClickListener {
            itemDetailViewModel.onSubmitItem(
                cartSellerId = mainViewModel.getUserCart()?.sellerId
            )
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            itemDetailViewModel.onFetchItemDetail(navArgs.property)
        }

        // Show toast
        viewLifecycleOwner.lifecycleScope.launch {
            itemDetailViewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        return binding.root
    }

    override fun onSuggestedItemClicked(itemBasic: SellerItemBasic) {
        findNavController(R.id.sellerItemDetailFragment)?.navigate(
            SellerItemDetailFragmentDirections.actionSellerItemDetailFragmentSelf(
                SellerItemDetailProperty(
                    sellerId = navArgs.property.sellerId,
                    itemId = itemBasic.id
                )
            )
        )
    }

    override fun onSuggestedItemChecked(itemBasic: SellerItemBasic, isChecked: Boolean) {
        itemDetailViewModel.onSuggestedItemChecked(itemBasic, isChecked)
    }

    private fun showErrorMessageDialog(message: String?) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.seller_item_detail_error_dialog_title))
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}