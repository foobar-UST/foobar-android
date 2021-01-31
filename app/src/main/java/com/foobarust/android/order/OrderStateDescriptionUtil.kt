package com.foobarust.android.order

import android.content.Context
import com.foobarust.android.R
import com.foobarust.domain.models.order.OrderState
import javax.inject.Inject

/**
 * Created by kevin on 1/30/21
 */

class OrderStateDescriptionUtil @Inject constructor(private val context: Context) {

    fun getOrderStateDescription(orderState: OrderState): String {
        val titleRes = when (orderState) {
            OrderState.PROCESSING -> R.string.order_state_description_processing
            OrderState.PREPARING -> R.string.order_state_description_preparing
            OrderState.IN_TRANSIT -> R.string.order_state_description_in_transit
            OrderState.READY_FOR_PICK_UP -> R.string.order_state_description_ready_for_pick_up
            OrderState.DELIVERED -> R.string.order_state_description_delivered
            OrderState.ARCHIVED -> R.string.order_state_description_archived
            OrderState.CANCELLED -> R.string.order_state_description_cancelled
        }

        return context.getString(titleRes)
    }
}