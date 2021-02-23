package com.foobarust.android.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.foobarust.android.databinding.DialogCartTimeOutBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.utils.AutoClearedValue
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 12/19/20
 */

@AndroidEntryPoint
class CartTimeoutDialog : BottomSheetDialogFragment() {

    private var binding: DialogCartTimeOutBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val cartItemsCount: Int by lazy { requireArguments().getInt(ARG_CART_ITEMS_COUNT) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCartTimeOutBinding.inflate(inflater, container, false).apply {
            cartItemsCount = this@CartTimeoutDialog.cartItemsCount
            lifecycleOwner = viewLifecycleOwner
        }

        // Dismiss the dialog
        binding.continueButton.setOnClickListener {
            dismiss()
        }

        // Clear user cart
        binding.clearCartButton.setOnClickListener {
            mainViewModel.onClearUserCart()
            dismiss()
        }

        return binding.root
    }

    companion object {
        const val TAG = "CartTimeoutDialog"
        const val ARG_CART_ITEMS_COUNT = "cart_items_count"

        @JvmStatic
        fun newInstance(cartItemsCount: Int): CartTimeoutDialog {
            return CartTimeoutDialog().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CART_ITEMS_COUNT, cartItemsCount)
                }
            }
        }
    }
}