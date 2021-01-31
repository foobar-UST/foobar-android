package com.foobarust.domain.models.order

/**
 * Created by kevin on 1/28/21
 *
 * @param priority indicates the display order of order items.
 */

enum class OrderState(val priority: Int) {
    PROCESSING(1),
    PREPARING(2),
    IN_TRANSIT(3),
    READY_FOR_PICK_UP(4),
    DELIVERED(0),
    ARCHIVED(5),
    CANCELLED(-1)
}