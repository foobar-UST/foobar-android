package com.foobarust.android.selleritem

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerItemDetailBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.SellerItemBasic
import com.foobarust.domain.models.seller.getNormalizedTitle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/12/20
 */

@AndroidEntryPoint
class SellerItemDetailFragment : FullScreenDialogFragment(R.layout.fragment_seller_item_detail),
    SellerItemDetailAdapter.SellerItemDetailAdapterListener {

    private val binding: FragmentSellerItemDetailBinding by viewBinding(FragmentSellerItemDetailBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val itemDetailViewModel: SellerItemDetailViewModel by viewModels()
    private val navArgs: SellerItemDetailFragmentArgs by navArgs()

    private var profileIncompleteSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            itemDetailViewModel.onFetchItemDetail(navArgs.property)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutFullscreen(aboveNavBar = true)

        binding.toolbar.applySystemWindowInsetsPadding(applyTop = true)

        // Remove listener on CollapsingToolbarLayout, so that toolbar top padding can work properly
        ViewCompat.setOnApplyWindowInsetsListener(binding.collapsingToolbarLayout, null)

        // Setup recycler view
        val itemDetailAdapter = SellerItemDetailAdapter(this)

        binding.recyclerView.run {
            adapter = itemDetailAdapter
            setHasFixedSize(true)
        }

        // Setup amount widget
        binding.amountIncrementButton.setOnClickListener {
            itemDetailViewModel.onAmountIncrement()
        }

        binding.amountDecrementButton.setOnClickListener {
            itemDetailViewModel.onAmountDecrement()
        }

        // Toolbar navigation
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

        // Item detail
        viewLifecycleOwner.lifecycleScope.launch {
            itemDetailViewModel.sellerItemDetail.collect { itemDetail ->
                itemDetail?.let {
                    val itemImageUrl = it.imageUrl
                    binding.itemImageLayout.isVisible = itemImageUrl != null
                    binding.itemImageView.contentDescription = itemDetail.getNormalizedTitle()
                    binding.appBarLayout.setExpanded(itemImageUrl != null)

                    if (itemImageUrl != null) {
                        binding.itemImageView.loadGlideUrl(
                            imageUrl = itemImageUrl,
                            centerCrop = true,
                            placeholder = R.drawable.placeholder_card
                        )
                    }
                }
            }
        }

        // List models
        viewLifecycleOwner.lifecycleScope.launch {
            itemDetailViewModel.sellerItemDetailListModels.collect {
                itemDetailAdapter.submitList(it)
            }
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            itemDetailViewModel.sellerItemDetailUiState.collect { uiState ->
                with(binding) {
                    loadingProgressBar.isVisible = uiState is SellerItemDetailUiState.Loading
                    loadErrorLayout.root.isVisible = uiState is SellerItemDetailUiState.Error
                }

                if (uiState is SellerItemDetailUiState.Error) {
                    showShortToast(uiState.message)
                }
            }
        }

        // Update state
        viewLifecycleOwner.lifecycleScope.launch {
            itemDetailViewModel.sellerItemDetailUpdateState.collect { uiState ->
                uiState?.let {
                    binding.submitButton.isInvisible = uiState is SellerItemDetailUiState.Loading

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
        }

        // Toolbar title
        viewLifecycleOwner.lifecycleScope.launch {
            itemDetailViewModel.toolbarTitle.collect {
                binding.toolbar.title = it
            }
        }

        // Submit button title
        viewLifecycleOwner.lifecycleScope.launch {
            itemDetailViewModel.submitButtonTitle.collect {
                binding.submitButton.text = it
            }
        }

        // Modify buttons
        viewLifecycleOwner.lifecycleScope.launch {
            itemDetailViewModel.showModifyButtons.collect {
                with(binding) {
                    modifyButtonsGroup.isVisible = it
                    submitProgressBar.isVisible = !it
                }
            }
        }

        // Show toolbar title only when collapsed
        viewLifecycleOwner.lifecycleScope.launch {
            binding.appBarLayout.state().collect {
                itemDetailViewModel.onToolbarScrollStateChanged(it)
            }
        }

        // Show toast
        viewLifecycleOwner.lifecycleScope.launch {
            itemDetailViewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        // Amount input
        viewLifecycleOwner.lifecycleScope.launch {
            itemDetailViewModel.amountsInput.collect { amount ->
                binding.amountTextView.text = amount.toString()
            }
        }

        // Check if user profile is completed
        viewLifecycleOwner.lifecycleScope.launch {
            itemDetailViewModel.isProfileCompleted.collect { isCompleted ->
                binding.itemSubmitLayout.isVisible = isCompleted
                showProfileIncompleteSnackbar(isShow = !isCompleted)
            }
        }
    }

    override fun onDestroyView() {
        profileIncompleteSnackbar = null
        super.onDestroyView()
    }

    override fun onSuggestedItemClicked(itemBasic: SellerItemBasic) {
        findNavController(R.id.sellerItemDetailFragment)?.navigate(
            SellerItemDetailFragmentDirections.actionItemDetailFragmentSelf(
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

    private fun showProfileIncompleteSnackbar(isShow: Boolean) {
        profileIncompleteSnackbar = if (isShow) {
            Snackbar.make(
                binding.coordinatorLayout,
                R.string.profile_require_data_for_ordering,
                Snackbar.LENGTH_INDEFINITE
            ).setActionPersist(R.string.seller_item_detail_profile_action) {
                navigateToProfile()
            }.apply {
                show()
            }
        } else {
            profileIncompleteSnackbar?.dismiss()
            null
        }
    }

    private fun navigateToProfile() {
        findNavController(R.id.sellerItemDetailFragment)?.navigate(
            SellerItemDetailFragmentDirections.actionItemDetailFragmentToProfileFragment()
        )
    }
}