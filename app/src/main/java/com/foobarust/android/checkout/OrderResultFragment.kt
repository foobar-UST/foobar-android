package com.foobarust.android.checkout

import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentOrderResultBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.getColorCompat
import com.foobarust.android.utils.themeColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

/**
 * Created by kevin on 1/9/21
 */

@AndroidEntryPoint
class OrderResultFragment : Fragment() {

    private var binding: FragmentOrderResultBinding by AutoClearedValue(this)
    private val checkoutViewModel: CheckoutViewModel by navGraphViewModels(R.id.navigation_checkout)
    private val navArgs: OrderResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderResultBinding.inflate(inflater, container, false)

        if (navArgs.property.isOrderSuccess()) {
            setupLayoutForSuccessState()
        } else {
            setupLayoutForFailureState()
        }

        // Hide submit button
        checkoutViewModel.onShowSubmitButton(isShow = false)

        // Observe dialog back press and navigate
        checkoutViewModel.backPressed.observe(viewLifecycleOwner) {
            if (navArgs.property.isOrderSuccess()) {
                checkoutViewModel.onDismissCheckoutDialog()
            } else {
                orderFailureNavigateToCartFragment()
            }
        }

        return binding.root
    }

    private fun setupLayoutForSuccessState() = with(binding) {
        checkoutViewModel.onUpdateToolbarTitle(
            title = getString(R.string.checkout_toolbar_title_order_result_success)
        )

        resultTitleTextView.text = getString(R.string.order_result_title_order_code)

        with(resultMessageTextView) {
            text = getString(
                R.string.order_result_format_order_code,
                navArgs.property.orderIdentifier
            )
            setTypeface(resultInfoTextView.typeface, Typeface.BOLD)
            setTextColor(
                requireContext().themeColor(R.attr.colorPrimary)
            )
        }

        resultImageView.setImageResource(R.drawable.undraw_successful_purchase)

        with(navigateButton) {
            text = getString(R.string.order_result_button_complete)
            setOnClickListener {
                checkoutViewModel.onDismissCheckoutDialog()
            }
        }
    }

    private fun setupLayoutForFailureState() = with(binding) {
        checkoutViewModel.onUpdateToolbarTitle(
            title = getString(R.string.checkout_toolbar_title_order_result_failed)
        )

        resultTitleTextView.visibility = View.GONE
        resultInfoTextView.visibility = View.GONE

        with(resultMessageTextView) {
            text = navArgs.property.errorMessage
            setTextColor(
                requireContext().getColorCompat(R.color.material_on_surface_emphasis_medium)
            )
        }

        resultImageView.setImageResource(R.drawable.undraw_cancel)

        // Return to cart fragment
        with(navigateButton) {
            text = getString(R.string.order_result_button_return)
            setOnClickListener {
                orderFailureNavigateToCartFragment()
            }
        }
    }

    private fun orderFailureNavigateToCartFragment() {
        findNavController(R.id.orderResultFragment)?.navigate(
            OrderResultFragmentDirections.actionOrderResultFragmentToCartFragment()
        )
    }
}

@Parcelize
data class OrderResultProperty(
    val orderId: String? = null,
    val orderIdentifier: String? = null,
    val orderLink: String? = null,
    val errorMessage: String? = null
) : Parcelable {
    fun isOrderSuccess(): Boolean = orderId != null
}