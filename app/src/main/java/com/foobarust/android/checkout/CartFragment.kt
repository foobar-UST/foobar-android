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
import com.foobarust.android.common.UiState
import com.foobarust.android.databinding.FragmentCartBinding
import com.foobarust.android.utils.*
import com.foobarust.domain.models.cart.UserCartItem
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
        binding = FragmentCartBinding.inflate(inflater, container, false).apply {
            viewModel = this@CartFragment.cartViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Set toolbar title
        cartViewModel.cartToolbarTitle.observe(viewLifecycleOwner) {
            checkoutViewModel.onUpdateToolbarTitle(title = it)
        }

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

        cartViewModel.cartListModels.observe(viewLifecycleOwner) {
            cartAdapter.submitList(it)
        }

        // Restore order notes when navigate back to CartFragment
        checkoutViewModel.savedOrderNotes?.let {
            cartViewModel.onRestoreOrderNotes(notes = it)
        }

        // Scroll to top when item inserted
        viewLifecycleOwner.lifecycleScope.launch {
            cartAdapter.scrollToTopWhenFirstItemInserted(binding.recyclerView)
        }

        // Show action snack bar
        cartViewModel.showSyncRequiredSnackBar.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                showSyncRequiredSnackBar()
            } else {
                hideSyncRequiredSnackBar()
            }
        }

        // Show toast
        cartViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Show updating progress bar
        cartViewModel.uiState.observe(viewLifecycleOwner) {
            checkoutViewModel.setShowUpdatingProgress(isShow = it is UiState.Loading)
        }

        // Set cart items count in app bar
        cartViewModel.cartItemsCount.observe(viewLifecycleOwner) {
            checkoutViewModel.onUpdateCartItemsCount(cartItemsCount = it)
        }

        // Show submit button
        cartViewModel.allowSubmitOrder.observe(viewLifecycleOwner) { allowSubmit ->
            checkoutViewModel.onShowSubmitButton(isShow = allowSubmit)
        }

        // Navigate to payment when submit button is clicked
        viewLifecycleOwner.lifecycleScope.launch {
            checkoutViewModel.onClickSubmitButton.collect {
                findNavController(R.id.cartFragment)?.navigate(
                    CartFragmentDirections.actionCartFragmentToPaymentFragment()
                )
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

    override fun onSectionOptionClicked(sellerId: String, sectionId: String?) {
        checkoutViewModel.onNavigateToSellerSection(sellerId, sectionId)
    }

    override fun onCartItemClicked(userCartItem: UserCartItem) {
        checkoutViewModel.onNavigateToSellerItemDetail(userCartItem)
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