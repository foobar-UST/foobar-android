package com.foobarust.android.sellerdetail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.foobarust.android.databinding.FragmentSellerItemDetailBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.showShortToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 10/12/20
 */

@AndroidEntryPoint
class SellerItemDetailFragment : BottomSheetDialogFragment() {

    private var binding: FragmentSellerItemDetailBinding by AutoClearedValue(this)
    private val viewModel: SellerItemDetailViewModel by viewModels()
    private val args: SellerItemDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Receive itemId argument and start fetching ItemDetail
        viewModel.onFetchItemDetail(property = args.sellerItemDetailProperty)
    }

    // Block back button when submitting to cart
    // OnBackPressedCallback doesn't work for dialog,
    // have to override the method instead.
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                if (!viewModel.isSubmittingToCart()) super.onBackPressed()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerItemDetailBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerItemDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup amount widget
        binding.amountIncrementButton.setOnClickListener {
            viewModel.onAmountIncremented()
        }

        binding.amountDecrementButton.setOnClickListener {
            viewModel.onAmountDecremented()
        }

        // Setup submit button
        binding.submitToCartButton.setOnClickListener {
            viewModel.onSubmitItemToCart()
        }

        // Dismiss dialog when there is network error
        viewModel.closeDialog.observe(viewLifecycleOwner) {
            dismiss()
        }

        // Prevent dismissing dialog when submitting to cart
        viewModel.isSubmittingToCart.observe(viewLifecycleOwner) { submitting ->
            requireDialog().setCancelable(!submitting)
        }

        // Show toast
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        return binding.root
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