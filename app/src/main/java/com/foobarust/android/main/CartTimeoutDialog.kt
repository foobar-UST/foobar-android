package com.foobarust.android.main

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.foobarust.android.databinding.DialogCartTimeOutBinding
import com.foobarust.android.utils.AutoClearedValue
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

/**
 * Created by kevin on 12/19/20
 */

private const val CART_TIMEOUT_PROPERTY = "cart_timeout_property"

@AndroidEntryPoint
class CartTimeoutDialog : BottomSheetDialogFragment() {

    private var binding: DialogCartTimeOutBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCartTimeOutBinding.inflate(inflater, container, false).apply {
            property = requireArguments().getParcelable(CART_TIMEOUT_PROPERTY) ?:
                throw IllegalArgumentException("Cart timeout property not found.")
            lifecycleOwner = viewLifecycleOwner
        }

        // Dismiss the dialog
        binding.continueButton.setOnClickListener { dismiss() }

        // Clear user cart
        binding.clearCartButton.setOnClickListener {
            mainViewModel.onClearUsersCart()
            dismiss()
        }

        return binding.root
    }

    companion object {
        const val TAG = "CartTimeoutDialog"

        fun newInstance(property: CartTimeoutProperty): CartTimeoutDialog {
            return CartTimeoutDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(CART_TIMEOUT_PROPERTY, property)
                }
            }
        }
    }
}

@Parcelize
data class CartTimeoutProperty(val cartItemsCount: Int) : Parcelable