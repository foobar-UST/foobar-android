package com.foobarust.android.checkout

import android.content.Context
import androidx.annotation.DrawableRes
import com.foobarust.android.R
import com.foobarust.domain.models.checkout.PaymentMethod
import javax.inject.Inject

/**
 * Created by kevin on 1/25/21
 */

class PaymentMethodUtil @Inject constructor(private val context: Context) {

    fun getPaymentMethodItem(paymentMethod: PaymentMethod): PaymentMethodItem {
        return PaymentMethodItem(
            identifier = paymentMethod.identifier,
            title = getMethodTitle(paymentMethod.identifier),
            description = getMethodDescription(paymentMethod.identifier),
            drawable = getMethodDrawable(paymentMethod.identifier)
        )
    }

    private fun getMethodTitle(identifier: String): String {
        return when (identifier) {
            "cash_on_delivery" -> context.getString(R.string.payment_methods_title_cash_on_delivery)
            "google_pay" -> context.getString(R.string.payment_methods_title_google_pay)
            else -> throw IllegalArgumentException("Unknown identifier: $identifier")
        }
    }

    private fun getMethodDescription(identifier: String): String {
        return when (identifier) {
            "cash_on_delivery" -> context.getString(R.string.payment_methods_description_cash_on_delivery)
            "google_pay" -> context.getString(R.string.payment_methods_description_google_pay)
            else -> throw IllegalArgumentException("Unknown identifier: $identifier")
        }
    }

    @DrawableRes
    private fun getMethodDrawable(identifier: String): Int {
        return when (identifier) {
            "cash_on_delivery" -> R.drawable.ic_local_atm
            "google_pay" -> R.drawable.ic_google_pay_mark
            else -> throw IllegalArgumentException("Unknown identifier: $identifier")
        }
    }
}

data class PaymentMethodItem(
    val identifier: String,
    val title: String,
    val description: String,
    @DrawableRes val drawable: Int
)