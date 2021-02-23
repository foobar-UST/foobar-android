package com.foobarust.data.mappers

import com.foobarust.data.constants.Constants.ORDER_STATE_ARCHIVED
import com.foobarust.data.constants.Constants.ORDER_STATE_CANCELLED
import com.foobarust.data.constants.Constants.ORDER_STATE_DELIVERED
import com.foobarust.data.constants.Constants.ORDER_STATE_IN_TRANSIT
import com.foobarust.data.constants.Constants.ORDER_STATE_PREPARING
import com.foobarust.data.constants.Constants.ORDER_STATE_PROCESSING
import com.foobarust.data.constants.Constants.ORDER_STATE_READY_FOR_PICK_UP
import com.foobarust.data.models.order.OrderBasicDto
import com.foobarust.data.models.order.OrderDetailDto
import com.foobarust.data.models.order.OrderItemDto
import com.foobarust.domain.models.order.*
import javax.inject.Inject

/**
 * Created by kevin on 1/28/21
 */

class OrderMapper @Inject constructor() {

    fun toOrderBasic(dto: OrderBasicDto): OrderBasic {
        return OrderBasic(
            id = dto.id!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            sellerId = dto.sellerId!!,
            sectionId = dto.sectionId,
            identifier = dto.identifier!!,
            imageUrl = dto.imageUrl,
            type = OrderType.values()[dto.type!!],
            state = toOrderState(dto.state!!),
            deliveryAddress = dto.deliveryAddress!!,
            deliveryAddressZh = dto.deliveryAddressZh,
            totalCost = dto.totalCost!!,
            createdAt = dto.createdAt!!.toDate(),
            updatedAt = dto.updatedAt!!.toDate()
        )
    }

    fun toOrderDetail(dto: OrderDetailDto): OrderDetail {
        return OrderDetail(
            id = dto.id!!,
            title = dto.title!!,
            titleZh = dto.titleZh,
            sellerId = dto.sellerId!!,
            sellerName = dto.sellerName!!,
            sellerNameZh = dto.sellerNameZh,
            sectionId = dto.sectionId,
            sectionTitle = dto.sectionTitle,
            sectionTitleZh = dto.sectionTitleZh,
            delivererId = dto.delivererId,
            identifier = dto.identifier!!,
            imageUrl = dto.imageUrl,
            type = OrderType.values()[dto.type!!],
            orderItems = dto.orderItems?.map { toOrderItem(it) } ?: emptyList(),
            state = toOrderState(dto.state!!),
            isPaid = dto.isPaid!!,
            paymentMethod = dto.paymentMethod!!,
            message = dto.message,
            deliveryLocation = dto.deliveryLocation!!.toGeolocation(),
            subtotalCost = dto.subtotalCost!!,
            deliveryCost = dto.deliveryCost!!,
            totalCost = dto.totalCost!!,
            createdAt = dto.createdAt!!.toDate(),
            updatedAt = dto.updatedAt!!.toDate()
        )
    }

    private fun toOrderItem(dto: OrderItemDto): OrderItem {
        return OrderItem(
            id = dto.id!!,
            itemId = dto.itemId!!,
            itemSellerId = dto.itemSellerId!!,
            itemTitle = dto.itemTitle!!,
            itemTitleZh = dto.itemTitleZh,
            itemPrice = dto.itemPrice!!,
            itemImageUrl = dto.itemImageUrl,
            amounts = dto.amounts!!,
            totalPrice = dto.totalPrice!!
        )
    }

    private fun toOrderState(state: String): OrderState {
        return when (state) {
            ORDER_STATE_PROCESSING -> OrderState.PROCESSING
            ORDER_STATE_PREPARING -> OrderState.PREPARING
            ORDER_STATE_IN_TRANSIT -> OrderState.IN_TRANSIT
            ORDER_STATE_READY_FOR_PICK_UP -> OrderState.READY_FOR_PICK_UP
            ORDER_STATE_DELIVERED -> OrderState.DELIVERED
            ORDER_STATE_ARCHIVED -> OrderState.ARCHIVED
            ORDER_STATE_CANCELLED -> OrderState.CANCELLED
            else -> throw IllegalStateException("Unknown order state: $state")
        }
    }
}