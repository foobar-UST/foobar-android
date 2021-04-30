package com.foobarust.testshared.serialize

import com.foobarust.domain.models.map.TravelMode
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.models.order.OrderDetail
import com.foobarust.domain.models.order.OrderState
import com.foobarust.domain.models.order.OrderType
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Created by kevin on 4/22/21
 */

@Serializable
data class OrderSerialized(
    val id: String,
    val title: String,
    val title_zh: String,
    val seller_id: String,
    val seller_name: String,
    val seller_name_zh: String? = null,
    val section_id: String? = null,
    val section_title: String? = null,
    val section_title_zh: String? = null,
    val deliverer_id: String? = null,
    val deliverer_location: GeoPointSerialized? = null,
    val deliverer_travel_mode: Int? = null,
    val identifier: String,
    val image_url: String? = null,
    val type: Int,
    val order_items: List<OrderItemSerialized>,
    val order_items_count: Int,
    val state: String,
    val is_paid: Boolean,
    val payment_method: String,
    val message: String? = null,
    val delivery_location: GeolocationSerialized,
    val subtotal_cost: Double,
    val delivery_cost: Double,
    val total_cost: Double,
    val verify_code: String
)

fun OrderSerialized.toOrderDetail(): OrderDetail {
    return OrderDetail(
        id = id, title = title, titleZh = title_zh, sellerId = seller_id, sellerName = seller_name,
        sellerNameZh = seller_name_zh, sectionId = section_id, sectionTitle = section_title,
        sectionTitleZh = section_title_zh, delivererId = deliverer_id,
        deliveryLocation = delivery_location.toGeolocation(),
        delivererLocation = deliverer_location?.toGeoPoint(),
        delivererTravelMode = deliverer_travel_mode?.let { TravelMode.values()[it] },
        identifier = identifier, imageUrl = image_url, type = OrderType.values()[type],
        orderItems = order_items.map { it.toOrderItem() },
        orderItemsCount = order_items_count,
        state = parseOrderState(state), isPaid = is_paid, paymentMethod = payment_method,
        message = message, subtotalCost = subtotal_cost, deliveryCost = delivery_cost,
        totalCost = total_cost, verifyCode = verify_code,
        createdAt = Date(), updatedAt = Date()
    )
}

internal fun OrderSerialized.toOrderBasic(): OrderBasic {
    return OrderBasic(
        id = id, title = title, titleZh = title_zh, sellerId = seller_id, sellerName = seller_name,
        sellerNameZh = seller_name_zh, sectionId = section_id, identifier = identifier,
        imageUrl = image_url, type = OrderType.values()[type], orderItemsCount = order_items_count,
        state = parseOrderState(state), totalCost = total_cost,
        createdAt = Date(), updatedAt = Date(),
        deliveryAddress = delivery_location.address,
        deliveryAddressZh = delivery_location.address_zh
    )
}

private fun parseOrderState(orderState: String): OrderState {
    return when (orderState) {
        "0_processing" -> OrderState.PROCESSING
        "1_preparing" -> OrderState.PREPARING
        "2_in_transit" -> OrderState.IN_TRANSIT
        "3_ready_for_pick_up" -> OrderState.READY_FOR_PICK_UP
        "4_delivered" -> OrderState.DELIVERED
        "5_archived" -> OrderState.ARCHIVED
        "6_cancelled" -> OrderState.CANCELLED
        else -> throw IllegalArgumentException("Invalid order state.")
    }
}