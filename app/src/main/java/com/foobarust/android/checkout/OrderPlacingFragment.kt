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
import com.foobarust.android.databinding.FragmentOrderPlacingBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 1/26/21
 */

@AndroidEntryPoint
class OrderPlacingFragment : Fragment() {

    private var binding: FragmentOrderPlacingBinding by AutoClearedValue(this)
    private val checkoutViewModel: CheckoutViewModel by navGraphViewModels(R.id.navigation_checkout)
    private val orderPlacingViewModel: OrderPlacingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val identifier = checkoutViewModel.savedPaymentIdentifier ?:
                throw Exception("Payment method not found.")

            orderPlacingViewModel.onStartPlacingOrder(
                orderMessage = checkoutViewModel.savedOrderNotes,
                paymentMethodIdentifier = identifier
            )
        }
    }

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

        // Prevent back pressed during transaction
        viewLifecycleOwner.lifecycleScope.launch {
            checkoutViewModel.backPressed.collect {
                if (!orderPlacingViewModel.isPlacingOrder()) {
                    findNavController().navigateUp()
                } else {
                    showShortToast(getString(R.string.order_placing_message))
                }
            }
        }

        // Clear order cache
        orderPlacingViewModel.placeOrderUiState.observe(viewLifecycleOwner) {
            checkoutViewModel.showLoadingProgressBar(it is PlaceOrderUiState.Loading)

            when (it) {
                is PlaceOrderUiState.Success -> checkoutViewModel.onClearCheckoutData()
                is PlaceOrderUiState.Failure -> showShortToast(it.message)
                else -> Unit
            }
        }

        // Navigate based on order result
        orderPlacingViewModel.navigateToOrderResult.observe(viewLifecycleOwner) {
            findNavController(R.id.orderPlacingFragment)?.navigate(
                OrderPlacingFragmentDirections.actionOrderPlacingFragmentToOrderResultFragment(
                    property = it
                )
            )
        }

        return binding.root
    }
}