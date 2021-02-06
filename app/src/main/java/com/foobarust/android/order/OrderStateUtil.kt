package com.foobarust.android.order

import android.content.Context
import com.foobarust.android.R
import com.foobarust.domain.models.order.OrderState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by kevin on 1/30/21
 */

class OrderStateUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getOrderStateTitle(orderState: OrderState): String {
        val titleRes = when (orderState) {
            OrderState.PROCESSING -> R.string.order_state_title_processing
            OrderState.PREPARING -> R.string.order_state_title_preparing
            OrderState.IN_TRANSIT -> R.string.order_state_title_in_transit
            OrderState.READY_FOR_PICK_UP -> R.string.order_state_title_ready_for_pick_up
            OrderState.DELIVERED -> R.string.order_state_title_delivered
            OrderState.ARCHIVED -> R.string.order_state_title_archived
            OrderState.CANCELLED -> R.string.order_state_title_cancelled
        }

        return context.getString(titleRes)
    }

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