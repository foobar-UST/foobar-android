package com.foobarust.android.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.foobarust.android.R
import com.foobarust.android.common.UiState
import com.foobarust.android.databinding.FragmentOrderPlacingBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 1/26/21
 */

@AndroidEntryPoint
class OrderPlacingFragment : Fragment() {

    private var binding: FragmentOrderPlacingBinding by AutoClearedValue(this)
    private val checkoutViewModel: CheckoutViewModel by navGraphViewModels(R.id.navigation_checkout)
    private val orderPlacingViewModel: OrderPlacingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderPlacingBinding.inflate(inflater, container, false)

        // Set toolbar title
        checkoutViewModel.onUpdateToolbarTitle(title = getString(R.string.checkout_toolbar_title_order_placing))

        // Hide submit button
        checkoutViewModel.onShowSubmitButton(isShow = false)

        // Observe dialog back press and navigate up
        checkoutViewModel.backPressed.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        // Show updating progress bar
        orderPlacingViewModel.uiState.observe(viewLifecycleOwner) {
            checkoutViewModel.setShowUpdatingProgress(isShow = it is UiState.Loading)
        }

        // Start placing order
        orderPlacingViewModel.onStartPlacingOrder(
            orderMessage = checkoutViewModel.savedOrderNotes,
            paymentMethodIdentifier = checkoutViewModel.savedPaymentIdentifier ?:
            throw Exception("Payment method not found.")
        )

        // Clear order cache
        orderPlacingViewModel.placeOrderState.observe(viewLifecycleOwner) {
            if (it is PlaceOrderState.Success) {
                checkoutViewModel.onClearPreviousOrderData()
            }
        }

        // Navigate based on order result
        orderPlacingViewModel.navigateToOrderResult.observe(viewLifecycleOwner) { property ->
            property?.let {
                findNavController(R.id.orderPlacingFragment)?.navigate(
                    OrderPlacingFragmentDirections.actionOrderPlacingFragmentToOrderResultFragment(
                        property = it
                    )
                )
            }
        }

        // Show toast
        orderPlacingViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        return binding.root
    }
}