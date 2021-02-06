package com.foobarust.domain.models.order

/**
 * Created by kevin on 1/28/21
 *
 * @param precedence indicates the display order of order items.
 */

enum class OrderState(val precedence: Int) {
    PROCESSING(0),
    PREPARING(1),
    IN_TRANSIT(2),      // Invalid for on-campus
    READY_FOR_PICK_UP(3),
    DELIVERED(4),
    ARCHIVED(5),
    CANCELLED(-1)
}