package com.foobarust.android.checkout

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
import com.foobarust.android.databinding.FragmentCartBinding
import com.foobarust.android.sellerdetail.SellerItemDetailProperty
import com.foobarust.android.states.UiState
import com.foobarust.android.utils.*
import com.foobarust.domain.models.cart.UserCartItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/29/20
 */

@AndroidEntryPoint
class CartFragment : Fragment(), CartAdapter.CartAdapterListener {

    private var binding: FragmentCartBinding by AutoClearedValue(this)
    private val checkoutViewModel: CheckoutViewModel by navGraphViewModels(R.id.navigation_checkout)
    private val cartViewModel: CartViewModel by viewModels()
    private var syncActionSnackBar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false).apply {
            viewModel = this@CartFragment.cartViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val cartAdapter = CartAdapter(this)

        binding.recyclerView.run {
            adapter = cartAdapter
            setHasFixedSize(true)
        }

        cartViewModel.cartListModels.observe(viewLifecycleOwner) {
            cartAdapter.submitList(it)
        }

        // Scroll to top when item inserted
        viewLifecycleOwner.lifecycleScope.launch {
            cartAdapter.scrollToTopWhenFirstItemInserted(binding.recyclerView)
        }

        // Show sync snack bar
        cartViewModel.isCartSyncRequired.observe(viewLifecycleOwner) { syncRequired ->
            if (syncRequired) {
                showSyncRequiredSnackBar()
            } else {
                hideSyncRequiredSnackBar()
            }
        }

        // Show cart timeout snack bar
        cartViewModel.showTimeoutMessage.observe(viewLifecycleOwner) {
            showCartTimeoutSnackBar()
        }

        // Toast
        cartViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // SnackBar
        cartViewModel.showSnackBarMessage.observe(viewLifecycleOwner) {
            showMessageSnackBar(message = it)
        }

        // Show updating progress bar in CheckoutFragment
        cartViewModel.uiState.observe(viewLifecycleOwner) {
            checkoutViewModel.setShowUpdatingProgress(isShow = it is UiState.Loading)
        }

        // Set cart items count in app bar
        cartViewModel.cartItemsCount.observe(viewLifecycleOwner) {
            checkoutViewModel.onUpdateCartItemsCount(cartItemsCount = it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        syncActionSnackBar = null
        super.onDestroyView()
    }

    override fun onNavigateToSellerDetail(sellerId: String) {
        checkoutViewModel.onNavigateToSellerDetail(sellerId)
    }

    override fun onNavigateToSellerMisc(sellerId: String) {
        checkoutViewModel.onNavigateToSellerMisc(sellerId)
    }

    override fun onCartPurchaseItemClicked(userCartItem: UserCartItem) {
        checkoutViewModel.onNavigateToSellerItemDetail(
            SellerItemDetailProperty(
                sellerId = userCartItem.itemSellerId,
                itemId = userCartItem.itemId,
                cartItemId = userCartItem.id,
                amounts = userCartItem.amounts
            )
        )
    }

    override fun onRemoveCartItem(userCartItem: UserCartItem) {
        cartViewModel.onRemoveCartItem(userCartItem)
    }

    override fun onClearCart() {
        // Show confirm dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.cart_clear_cart_dialog_title))
            .setMessage(getString(R.string.cart_clear_Cart_dialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> cartViewModel.onClearUsersCart() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onPlaceOrder() {
        findNavController().navigate(
            CartFragmentDirections.actionCartFragmentToPaymentFragment()
        )
    }

    override fun onUpdateNotes(notes: String) {

    }

    override fun onChooseDeliveryOption() {
        findNavController().navigate(
            CartFragmentDirections.actionCartFragmentToDeliveryOptionsBottomSheet(
                deliveryOptionProperties = cartViewModel.getDeliveryOptions()
                    .map { it.toDeliveryOptionProperty() }
                    .toTypedArray()
            )
        )
    }

    private fun showSyncRequiredSnackBar() {
        syncActionSnackBar = Snackbar.make(
            binding.coordinatorLayout,
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

    private fun showCartTimeoutSnackBar() {
        Snackbar.make(
            binding.coordinatorLayout,
            R.string.cart_timeout_message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun hideSyncRequiredSnackBar() {
        syncActionSnackBar?.dismiss()
        syncActionSnackBar = null
    }

    private fun showMessageSnackBar(message: String) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }
}