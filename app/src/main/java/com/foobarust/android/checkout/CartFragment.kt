package com.foobarust.android.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentCartBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.scrollToTopWhenFirstItemInserted
import com.foobarust.android.utils.showShortToast
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.models.cart.getNormalizedTitle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/29/20
 */

@AndroidEntryPoint
class CartFragment : Fragment(), CartAdapter.CartAdapterListener {

    private var binding: FragmentCartBinding by AutoClearedValue(this)
    private val checkoutViewModel: CheckoutViewModel by navGraphViewModels(R.id.navigation_checkout)
    private val cartViewModel: CartViewModel by viewModels()
    private var indefiniteSnackBar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        // Set submit button title
        checkoutViewModel.onUpdateSubmitButtonTitle(
            title = getString(R.string.checkout_submit_button_title_cart)
        )

        // Setup recycler view
        val cartAdapter = CartAdapter(this)

        binding.recyclerView.run {
            adapter = cartAdapter
            setHasFixedSize(true)
        }

        // Swipe to refresh layout
        binding.swipeRefreshLayout.setOnRefreshListener {
            cartViewModel.onFetchCart()
        }

        // Restore order notes when navigate back to CartFragment
        checkoutViewModel.savedOrderNotes?.let {
            cartViewModel.onRestoreOrderNotes(notes = it)
        }

        // User cart
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.userCart.collect { userCart ->
                val toolbarTitle = userCart?.getNormalizedTitle() ?:
                    getString(R.string.checkout_toolbar_title_cart)
                checkoutViewModel.onUpdateToolbarTitle(toolbarTitle)

                if (userCart?.syncRequired == true) {
                    showSyncRequiredSnackBar()
                } else {
                    hideSyncRequiredSnackBar()
                }
            }
        }

        // Cart items count
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartItems.collect {
                checkoutViewModel.onUpdateCartItemsCount(it.size)
            }
        }

        // Cart list models
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartListModels.collect {
                cartAdapter.submitList(it)
            }
        }

        // Ui state
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartUiState.collect { uiState ->
                checkoutViewModel.showLoadingProgressBar(uiState is CartUiState.Loading)

                if (uiState is CartUiState.Error) {
                    showShortToast(uiState.message)
                }
            }
        }

        // Update state
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartUpdateState.collect {
                checkoutViewModel.showLoadingProgressBar(it is CartUpdateState.Loading)
                checkoutViewModel.onShowSubmitButton(it !is CartUpdateState.Disabled)

                if (it is CartUpdateState.Error) {
                    showShortToast(it.message)
                }
            }
        }

        // Scroll to top when item inserted
        viewLifecycleOwner.lifecycleScope.launch {
            cartAdapter.scrollToTopWhenFirstItemInserted(binding.recyclerView)
        }

        // Show updating progress bar
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartUiState.collect {
                checkoutViewModel.showLoadingProgressBar(isShow = it is CartUiState.Loading)
            }
        }

        // Navigate to payment when submit button is clicked
        viewLifecycleOwner.lifecycleScope.launch {
            checkoutViewModel.onClickSubmitButton.collect {
                findNavController(R.id.cartFragment)?.navigate(
                    CartFragmentDirections.actionCartFragmentToPaymentFragment()
                )
            }
        }

        // Finish swipe to refresh
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.finishSwipeRefresh.collect {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        hideSyncRequiredSnackBar()
        super.onDestroyView()
    }

    override fun onAddMoreItemClicked(sellerId: String, sectionId: String?) {
        checkoutViewModel.onNavigateToSellerDetail(sellerId, sectionId)
    }

    override fun onSellerMiscOptionClicked(sellerId: String) {
        checkoutViewModel.onNavigateToSellerMisc(sellerId)
    }

    override fun onSectionOptionClicked(sectionId: String) {
        checkoutViewModel.onNavigateToSellerSection(sectionId)
    }

    override fun onCartItemClicked(userCartItem: UserCartItem) {
        checkoutViewModel.onNavigateToSellerItemDetail(userCartItem)
    }

    override fun onRemoveCartItem(userCartItem: UserCartItem) {
        cartViewModel.onRemoveCartItem(userCartItem)
    }

    override fun onClearCart() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.cart_clear_cart_dialog_title))
            .setMessage(getString(R.string.cart_clear_Cart_dialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> cartViewModel.onClearUsersCart() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onUpdateOrderNotes(notes: String) {
        checkoutViewModel.savedOrderNotes = notes
    }

    private fun showSyncRequiredSnackBar() {
        indefiniteSnackBar = Snackbar.make(
            binding.frameLayout,
            R.string.cart_sync_required_message,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(R.string.cart_sync_required_action_refresh) {
                cartViewModel.onSyncUserCart()
                dismiss()
            }
            show()
        }
    }

    private fun hideSyncRequiredSnackBar() {
        indefiniteSnackBar?.dismiss()
        indefiniteSnackBar = null
    }
}