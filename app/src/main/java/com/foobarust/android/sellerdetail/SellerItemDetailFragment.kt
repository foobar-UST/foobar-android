package com.foobarust.android.sellerdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerItemDetailBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.showShortToast
import com.foobarust.domain.states.getSuccessDataOr
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/12/20
 */

@AndroidEntryPoint
class SellerItemDetailFragment : BottomSheetDialogFragment() {

    private var binding: FragmentSellerItemDetailBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val itemDetailViewModel: SellerItemDetailViewModel by viewModels()
    private val args: SellerItemDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Receive itemId argument and start fetching ItemDetail
        itemDetailViewModel.onFetchItemDetail(property = args.sellerItemDetailProperty)
    }

    // Block back button when submitting to cart
    // OnBackPressedCallback doesn't work for dialog,
    // have to override the method instead.
    /*
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                if (!viewModel.isSubmittingToCart()) super.onBackPressed()
            }
        }
    }

     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerItemDetailBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerItemDetailFragment.itemDetailViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup amount widget
        binding.amountIncrementButton.setOnClickListener {
            itemDetailViewModel.onAmountIncremented()
        }

        binding.amountDecrementButton.setOnClickListener {
            itemDetailViewModel.onAmountDecremented()
        }

        // Setup submit button
        binding.submitToCartButton.setOnClickListener {
            submitItemToCart()
        }

        // Dismiss dialog when there is network error
        itemDetailViewModel.dismissDialog.observe(viewLifecycleOwner) {
            dismiss()
        }

        // Prevent dismissing dialog when submitting to cart
        itemDetailViewModel.cartItemSubmitting.observe(viewLifecycleOwner) { submitting ->
            requireDialog().setCancelable(!submitting)
        }

        // Show toast
        itemDetailViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Diff seller
        itemDetailViewModel.showDiffSellerDialog.observe(viewLifecycleOwner) {
            showDiffSellerDialog()
        }

        return binding.root
    }

    private fun submitItemToCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            val userCart = mainViewModel.userCart.first().getSuccessDataOr(null)
            userCart?.let {
                itemDetailViewModel.onSubmitItemToCart(userCart = it)
            }
        }
    }

    private fun showDiffSellerDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.seller_item_detail_diff_seller_title))
            .setMessage(getString(R.string.seller_item_detail_diff_seller_message))
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /*
    private fun getScreenHeightPixels(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())

            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)

            displayMetrics.heightPixels
        }
    }

     */
}