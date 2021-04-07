package com.foobarust.data.mappers

import com.foobarust.data.constants.Constants
import com.foobarust.data.constants.Constants.ORDER_STATE_ARCHIVED
import com.foobarust.data.constants.Constants.ORDER_STATE_CANCELLED
import com.foobarust.data.constants.Constants.ORDER_STATE_DELIVERED
import com.foobarust.data.constants.Constants.ORDER_STATE_IN_TRANSIT
import com.foobarust.data.constants.Constants.ORDER_STATE_PREPARING
import com.foobarust.data.constants.Constants.ORDER_STATE_PROCESSING
import com.foobarust.data.constants.Constants.ORDER_STATE_READY_FOR_PICK_UP
import com.foobarust.data.models.order.*
import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.map.TravelMode
import com.foobarust.domain.models.order.*
import javax.inject.Inject

/**
 * Created by kevin on 1/28/21
 */

class OrderMapper @Inject constructor() {

    fun fromOrderBasicNetworkDtoToOrderBasic(networkDto: OrderBasicNetworkDto): OrderBasic {
        return OrderBasic(
            id = networkDto.id!!,
            title = networkDto.title!!,
            titleZh = networkDto.titleZh,
            sellerId = networkDto.sellerId!!,
            sellerName = networkDto.sellerName!!,
            sellerNameZh = networkDto.sellerNameZh,
            sectionId = networkDto.sectionId,
            identifier = networkDto.identifier!!,
            imageUrl = networkDto.imageUrl,
            type = OrderType.values()[networkDto.type!!],
            orderItemsCount = networkDto.orderItemsCount!!,
            state = toOrderState(networkDto.state!!),
            deliveryAddress = networkDto.deliveryAddress!!,
            deliveryAddressZh = networkDto.deliveryAddressZh,
            totalCost = networkDto.totalCost!!,
            createdAt = networkDto.createdAt!!.toDate(),
            updatedAt = networkDto.updatedAt!!.toDate()
        )
    }

    fun fromOrderBasicCacheDtoToOrderBasic(cacheDto: OrderBasicCacheDto): OrderBasic {
        return OrderBasic(
            id = cacheDto.id,
            title = cacheDto.title!!,
            titleZh = cacheDto.titleZh,
            sellerId = cacheDto.sellerId!!,
            sellerName = cacheDto.sellerName!!,
            sellerNameZh = cacheDto.sellerNameZh,
            sectionId = cacheDto.sectionId,
            identifier = cacheDto.identifier!!,
            imageUrl = cacheDto.imageUrl,
            type = OrderType.values()[cacheDto.type!!],
            orderItemsCount = cacheDto.orderItemsCount!!,
            state = toOrderState(cacheDto.state!!),
            deliveryAddress = cacheDto.deliveryAddress!!,
            deliveryAddressZh = cacheDto.deliveryAddressZh,
            totalCost = cacheDto.totalCost!!,
            createdAt = cacheDto.createdAt!!,
            updatedAt = cacheDto.updatedAt!!
        )
    }

    fun toOrderBasicCacheDto(orderBasic: OrderBasic): OrderBasicCacheDto {
        return OrderBasicCacheDto(
            id = orderBasic.id,
            title = orderBasic.title,
            titleZh = orderBasic.titleZh,
            sellerId = orderBasic.sellerId,
            sellerName = orderBasic.sellerName,
            sellerNameZh = orderBasic.sellerNameZh,
            sectionId = orderBasic.sectionId,
            identifier = orderBasic.identifier,
            imageUrl = orderBasic.imageUrl,
            type = orderBasic.type.ordinal,
            orderItemsCount = orderBasic.orderItemsCount,
            state = fromOrderState(orderBasic.state),
            deliveryAddress = orderBasic.deliveryAddress,
            deliveryAddressZh = orderBasic.deliveryAddressZh,
            totalCost = orderBasic.totalCost,
            createdAt = orderBasic.createdAt,
            updatedAt = orderBasic.updatedAt
        )
    }

    fun fromOrderDetailNetworkDtoToOrderDetail(networkDto: OrderDetailNetworkDto): OrderDetail {
        val orderItems = networkDto.orderItems?.map {
            fromOrderItemNetworkDtoToOrderItem(it)
        } ?: emptyList()

        return OrderDetail(
            id = networkDto.id!!,
            title = networkDto.title!!,
            titleZh = networkDto.titleZh,
            sellerId = networkDto.sellerId!!,
            sellerName = networkDto.sellerName!!,
            sellerNameZh = networkDto.sellerNameZh,
            sectionId = networkDto.sectionId,
            sectionTitle = networkDto.sectionTitle,
            sectionTitleZh = networkDto.sectionTitleZh,
            delivererId = networkDto.delivererId,
            delivererLocation = networkDto.delivererLocation?.toGeolocationPoint(),
            delivererTravelMode = networkDto.delivererTravelMode?.let { toTravelMode(it) },
            identifier = networkDto.identifier!!,
            imageUrl = networkDto.imageUrl,
            type = OrderType.values()[networkDto.type!!],
            orderItems = orderItems,
            orderItemsCount = networkDto.orderItemsCount!!,
            state = toOrderState(networkDto.state!!),
            isPaid = networkDto.isPaid!!,
            paymentMethod = networkDto.paymentMethod!!,
            message = networkDto.message,
            deliveryLocation = networkDto.deliveryLocation!!.toGeolocation(),
            subtotalCost = networkDto.subtotalCost!!,
            deliveryCost = networkDto.deliveryCost!!,
            totalCost = networkDto.totalCost!!,
            verifyCode = networkDto.verifyCode!!,
            createdAt = networkDto.createdAt!!.toDate(),
            updatedAt = networkDto.updatedAt!!.toDate()
        )
    }

    fun fromOrderDetailWithItemsCacheDtoToOrderDetail(
        cacheDto: OrderDetailWithItemsCacheDto
    ): OrderDetail {
        val (orderDetailCacheDto, orderDetailItemsCacheDtos) = cacheDto

        val orderItems = orderDetailItemsCacheDtos.map {
            fromOrderItemCacheDtoToOrderItem(it)
        }

        val deliveryLocation = Geolocation(
            address = orderDetailCacheDto.deliveryLocationAddress!!,
            addressZh = orderDetailCacheDto.deliveryLocationAddressZh,
            locationPoint = GeolocationPoint(
                latitude = orderDetailCacheDto.deliveryLocationGeoPointLat!!,
                longitude = orderDetailCacheDto.deliveryLocationGeoPointLong!!
            )
        )

        return OrderDetail(
            id = orderDetailCacheDto.id,
            title = orderDetailCacheDto.title!!,
            titleZh = orderDetailCacheDto.titleZh,
            sellerId = orderDetailCacheDto.sellerId!!,
            sellerName = orderDetailCacheDto.sellerName!!,
            sellerNameZh = orderDetailCacheDto.sellerNameZh,
            sectionId = orderDetailCacheDto.sectionId,
            sectionTitle = orderDetailCacheDto.sectionTitle,
            sectionTitleZh = orderDetailCacheDto.sectionTitleZh,
            delivererId = orderDetailCacheDto.delivererId,
            delivererLocation = orderDetailCacheDto.delivererLocation?.let {
                fromStringToGeoLocationPoint(it)
            },
            delivererTravelMode = orderDetailCacheDto.delivererTravelMode?.let {
                toTravelMode(it)
            },
            identifier = orderDetailCacheDto.identifier!!,
            imageUrl = orderDetailCacheDto.imageUrl,
            type = OrderType.values()[orderDetailCacheDto.type!!],
            orderItemsCount = orderDetailCacheDto.orderItemsCount!!,
            orderItems = orderItems,
            state = toOrderState(orderDetailCacheDto.state!!),
            isPaid = orderDetailCacheDto.isPaid!!,
            paymentMethod = orderDetailCacheDto.paymentMethod!!,
            message = orderDetailCacheDto.message,
            deliveryLocation = deliveryLocation,
            subtotalCost = orderDetailCacheDto.subtotalCost!!,
            deliveryCost = orderDetailCacheDto.deliveryCost!!,
            totalCost = orderDetailCacheDto.totalCost!!,
            verifyCode = orderDetailCacheDto.verifyCode!!,
            createdAt = orderDetailCacheDto.createdAt!!,
            updatedAt = orderDetailCacheDto.updatedAt!!
        )
    }

    fun toOrderDetailCacheDto(orderDetail: OrderDetail): OrderDetailCacheDto {
        return OrderDetailCacheDto(
            id = orderDetail.id,
            title = orderDetail.title,
            titleZh = orderDetail.titleZh,
            sellerId = orderDetail.sellerId,
            sellerName = orderDetail.sellerName,
            sellerNameZh = orderDetail.sellerNameZh,
            sectionId = orderDetail.sectionId,
            sectionTitle = orderDetail.sectionTitle,
            sectionTitleZh = orderDetail.sectionTitleZh,
            delivererId = orderDetail.delivererId,
            delivererLocation = orderDetail.delivererLocation?.let {
                fromGeoLocationPointToString(it)
            },
            delivererTravelMode = orderDetail.delivererTravelMode?.let {
                fromTravelMode(it)
            },
            identifier = orderDetail.identifier,
            imageUrl = orderDetail.imageUrl,
            type = orderDetail.type.ordinal,
            orderItemsCount = orderDetail.orderItemsCount,
            state = fromOrderState(orderDetail.state),
            isPaid = orderDetail.isPaid,
            paymentMethod = orderDetail.paymentMethod,
            message = orderDetail.message,
            deliveryLocationAddress = orderDetail.deliveryLocation.address,
            deliveryLocationAddressZh = orderDetail.deliveryLocation.addressZh,
            deliveryLocationGeoPointLat = orderDetail.deliveryLocation.locationPoint.latitude,
            deliveryLocationGeoPointLong = orderDetail.deliveryLocation.locationPoint.longitude,
            subtotalCost = orderDetail.subtotalCost,
            deliveryCost = orderDetail.deliveryCost,
            totalCost = orderDetail.totalCost,
            verifyCode = orderDetail.verifyCode,
            createdAt = orderDetail.createdAt,
            updatedAt = orderDetail.updatedAt
        )
    }

    fun toOrderItemCacheDtos(orderDetail: OrderDetail): List<OrderItemCacheDto> {
        return orderDetail.orderItems.map { orderItem ->
            OrderItemCacheDto(
                id = orderItem.id,
                orderId = orderDetail.id,
                itemId = orderItem.itemId,
                itemSellerId = orderItem.itemSellerId,
                itemTitle = orderItem.itemTitle,
                itemTitleZh = orderItem.itemTitleZh,
                itemPrice = orderItem.itemPrice,
                itemImageUrl = orderItem.itemImageUrl,
                amounts = orderItem.amounts,
                totalPrice = orderItem.totalPrice
            )
        }
    }

    fun toSubmitOrderRatingRequest(orderRating: OrderRating): SubmitOrderRatingRequest {
        return SubmitOrderRatingRequest(
            orderId = orderRating.orderId,
            orderRating = orderRating.orderRating,
            deliveryRating = orderRating.deliveryRating
        )
    }

    private fun fromOrderItemNetworkDtoToOrderItem(networkDto: OrderItemNetworkDto): OrderItem {
        return OrderItem(
            id = networkDto.id!!,
            itemId = networkDto.itemId!!,
            itemSellerId = networkDto.itemSellerId!!,
            itemTitle = networkDto.itemTitle!!,
            itemTitleZh = networkDto.itemTitleZh,
            itemPrice = networkDto.itemPrice!!,
            itemImageUrl = networkDto.itemImageUrl,
            amounts = networkDto.amounts!!,
            totalPrice = networkDto.totalPrice!!
        )
    }

    private fun fromOrderItemCacheDtoToOrderItem(cacheDto: OrderItemCacheDto): OrderItem {
        return OrderItem(
            id = cacheDto.id,
            itemId = cacheDto.itemId!!,
            itemSellerId = cacheDto.itemSellerId!!,
            itemTitle = cacheDto.itemTitle!!,
            itemTitleZh = cacheDto.itemTitleZh,
            itemPrice = cacheDto.itemPrice!!,
            itemImageUrl = cacheDto.itemImageUrl,
            amounts = cacheDto.amounts!!,
            totalPrice = cacheDto.totalPrice!!
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

    private fun fromOrderState(orderState: OrderState): String {
        return when (orderState) {
            OrderState.PROCESSING -> ORDER_STATE_PROCESSING
            OrderState.PREPARING -> ORDER_STATE_PREPARING
            OrderState.IN_TRANSIT -> ORDER_STATE_IN_TRANSIT
            OrderState.READY_FOR_PICK_UP -> ORDER_STATE_READY_FOR_PICK_UP
            OrderState.DELIVERED -> ORDER_STATE_DELIVERED
            OrderState.ARCHIVED -> ORDER_STATE_ARCHIVED
            OrderState.CANCELLED -> ORDER_STATE_CANCELLED
        }
    }

    private fun fromGeoLocationPointToString(locationPoint: GeolocationPoint): String {
        return "${locationPoint.latitude},${locationPoint.longitude}"
    }

    private fun fromStringToGeoLocationPoint(locationPoint: String): GeolocationPoint {
        val output = locationPoint.split(',')
        return GeolocationPoint(
            latitude = output[0].toDouble(),
            longitude = output[1].toDouble()
        )
    }

    private fun fromTravelMode(travelMode: TravelMode): String {
        return when (travelMode) {
            TravelMode.DRIVING -> Constants.MAPS_DIRECTIONS_MODE_DRIVING
            TravelMode.WALKING -> Constants.MAPS_DIRECTIONS_MODE_WALKING
        }
    }

    private fun toTravelMode(travelMode: String): TravelMode {
        return when (travelMode) {
            Constants.MAPS_DIRECTIONS_MODE_DRIVING -> TravelMode.DRIVING
            Constants.MAPS_DIRECTIONS_MODE_WALKING -> TravelMode.WALKING
            else -> throw IllegalStateException("Unknown travel mode: $travelMode")
        }
    }
}