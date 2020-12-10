package com.foobarust.android.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentCartBinding
import com.foobarust.android.sellerdetail.SellerDetailProperty
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.firstItemInsertedScrollTop
import com.foobarust.android.utils.showShortToast
import com.foobarust.domain.models.cart.UserCartItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/29/20
 */

@AndroidEntryPoint
class CartFragment : DialogFragment(), CartAdapter.CartAdapterListener {

    private var binding: FragmentCartBinding by AutoClearedValue(this)
    private val viewModel: CartViewModel by viewModels()
    private var syncActionSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight)

        viewModel.onFetchCartItems()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false).apply {
            viewModel = this@CartFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val cartAdapter = CartAdapter(this)

        binding.recyclerView.run {
            adapter = cartAdapter
            setHasFixedSize(true)
        }

        viewModel.cartListModels.observe(viewLifecycleOwner) {
            cartAdapter.submitList(it)
        }

        // Scroll to top when item inserted
        viewLifecycleOwner.lifecycleScope.launch {
            cartAdapter.firstItemInsertedScrollTop(binding.recyclerView)
        }

        // Close cart button
        binding.toolbar.setNavigationOnClickListener { dismiss() }

        // Show sync snack bar
        viewModel.showSyncAction.observe(viewLifecycleOwner) { syncRequired ->
            if (syncRequired) {
                showSyncRequiredSnackBar()
            } else {
                hideSyncRequiredSnackBar()
            }
        }

        // Show cart timeout snack bar
        viewModel.showCartTimeoutMessage.observe(viewLifecycleOwner) {
            showCartTimeoutSnackBar()
        }

        // Toast
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        syncActionSnackBar = null
        super.onDestroyView()
    }

    override fun onNavigateToSellerDetail(sellerId: String) {
        findNavController(R.id.cartFragment)?.navigate(
            CartFragmentDirections.actionCartFragmentToSellerDetailFragment(
                sellerDetailProperty = SellerDetailProperty(id = sellerId)
            )
        )
    }

    override fun onNavigateToSellerMisc(sellerId: String) {
        findNavController(R.id.cartFragment)?.navigate(
            CartFragmentDirections.actionCartFragmentToSellerMiscFragment(sellerId)
        )
    }

    override fun onRemoveCartItem(userCartItem: UserCartItem) {
        viewModel.onRemoveCartItem(userCartItem)
    }

    override fun onClearCart() {
        // Show clear cart confirm dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.cart_clear_cart_dialog_title))
            .setMessage(getString(R.string.cart_clear_Cart_dialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.onClearCart() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onPlaceOrder() {
        showShortToast("onPlaceOrder")
    }

    private fun showSyncRequiredSnackBar() {
        syncActionSnackBar = Snackbar.make(
            binding.coordinatorLayout,
            R.string.cart_sync_required_message,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(R.string.cart_sync_required_action_refresh) {
                // TODO: sync action
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
}