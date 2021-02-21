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
import com.foobarust.android.databinding.FragmentPaymentBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.showShortToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        paymentViewModel.paymentItemModels.observe(viewLifecycleOwner) {
            paymentAdapter.submitList(it)
        }

        paymentViewModel.paymentUiState.observe(viewLifecycleOwner) {
            checkoutViewModel.showLoadingProgressBar(it is PaymentUiState.Loading)
            checkoutViewModel.onShowSubmitButton(it is PaymentUiState.Ready)

            if (it is PaymentUiState.Error) {
                showShortToast(it.message)
            }
        }

        // Swipe refresh layout
        binding.swipeRefreshLayout.setOnRefreshListener {
            paymentViewModel.onFetchPaymentMethods(isSwipeRefresh = true)
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            paymentViewModel.onFetchPaymentMethods()
        }

        // Restore selected payment method when navigate back to CartFragment
        checkoutViewModel.savedPaymentIdentifier?.let {
            paymentViewModel.onSelectPaymentMethod(identifier = it)
        }

        // Set toolbar title
        checkoutViewModel.onUpdateToolbarTitle(
            title = getString(R.string.checkout_toolbar_title_payment)
        )

        // Set submit button title
        checkoutViewModel.onUpdateSubmitButtonTitle(
            title = getString(R.string.checkout_submit_button_title_payment)
        )

        // Observe dialog back press and navigate up
        viewLifecycleOwner.lifecycleScope.launch {
            checkoutViewModel.backPressed.collect {
                findNavController().navigateUp()
            }
        }

        // Navigate to order success when submit button is clicked
        viewLifecycleOwner.lifecycleScope.launch {
            checkoutViewModel.onClickSubmitButton.collect {
                showConfirmDialog()
            }
        }

        // Show toast
        viewLifecycleOwner.lifecycleScope.launch {
            paymentViewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        // Finish swipe refreshing
        viewLifecycleOwner.lifecycleScope.launch {
            paymentViewModel.finishSwipeRefresh.collect {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        return binding.root
    }

    override fun onPaymentMethodClicked(identifier: String) {
        checkoutViewModel.savedPaymentIdentifier = identifier
        paymentViewModel.onSelectPaymentMethod(identifier)
    }

    private fun showConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.checkout_confirm_dialog_title))
            .setMessage(resources.getString(R.string.checkout_confirm_dialog_message))
            .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                findNavController(R.id.paymentFragment)?.navigate(
                    PaymentFragmentDirections.actionPaymentFragmentToOrderPlacingFragment()
                )
            }
            .setNegativeButton(resources.getString(android.R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}