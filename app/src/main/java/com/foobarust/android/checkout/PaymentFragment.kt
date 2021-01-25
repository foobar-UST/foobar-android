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
import com.foobarust.android.common.UiState
import com.foobarust.android.databinding.FragmentPaymentBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 1/9/21
 */

@AndroidEntryPoint
class PaymentFragment : Fragment(), PaymentAdapter.PaymentAdapterListener {

    private var binding: FragmentPaymentBinding by AutoClearedValue(this)
    private val checkoutViewModel: CheckoutViewModel by navGraphViewModels(R.id.navigation_checkout)
    private val paymentViewModel: PaymentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false).apply {
            viewModel = this@PaymentFragment.paymentViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup payment list
        val paymentAdapter = PaymentAdapter(this)

        binding.paymentMethodsRecyclerView.run {
            adapter = paymentAdapter
            // Disable collapsing toolbar scrolling
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
        }

        paymentViewModel.paymentMethodItemModels.observe(viewLifecycleOwner) {
            paymentAdapter.submitList(it)
        }

        // Restore selected payment method when navigate back to CartFragment
        paymentViewModel.onRestoreSelectPaymentMethod(
            identifier = checkoutViewModel.savedPaymentIdentifier
        )

        // Set toolbar title
        checkoutViewModel.onUpdateToolbarTitle(
            title = getString(R.string.checkout_toolbar_title_payment)
        )

        // Set submit button title
        checkoutViewModel.onUpdateSubmitButtonTitle(
            title = getString(R.string.checkout_submit_button_title_payment)
        )

        // Observe dialog back press and navigate up
        checkoutViewModel.backPressed.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        // Navigate to order success when submit button is clicked
        viewLifecycleOwner.lifecycleScope.launch {
            checkoutViewModel.onClickSubmitButton.collect {
                findNavController(R.id.paymentFragment)?.navigate(
                    PaymentFragmentDirections.actionPaymentFragmentToOrderPlacingFragment()
                )
            }
        }

        // Show updating progress bar
        paymentViewModel.uiState.observe(viewLifecycleOwner) {
            checkoutViewModel.setShowUpdatingProgress(isShow = it is UiState.Loading)
        }

        // Show submit button
        paymentViewModel.allowProceedPayment.observe(viewLifecycleOwner) { allowPayment ->
            checkoutViewModel.onShowSubmitButton(isShow = allowPayment)
        }

        // Show toast
        paymentViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        return binding.root
    }

    override fun onPaymentMethodClicked(identifier: String) {
        checkoutViewModel.savedPaymentIdentifier = identifier
        paymentViewModel.onRestoreSelectPaymentMethod(identifier)
    }
}