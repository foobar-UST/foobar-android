package com.foobarust.android.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.databinding.DialogCartTimeOutBinding
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
    private val navArgs: CartTimeoutDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCartTimeOutBinding.inflate(inflater, container, false).apply {
            cartItemsCount = navArgs.cartItemsCount
            lifecycleOwner = viewLifecycleOwner
        }

        // Dismiss the dialog
        binding.continueButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Clear user cart
        binding.clearCartButton.setOnClickListener {
            mainViewModel.onClearUsersCart()
            findNavController().navigateUp()
        }

        return binding.root
    }
}