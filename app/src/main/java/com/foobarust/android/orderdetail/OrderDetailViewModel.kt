package com.foobarust.android.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.checkout.PaymentMethodUtil
import com.foobarust.android.order.OrderStateUtil
import com.foobarust.android.orderdetail.OrderDetailListModel.*
import com.foobarust.android.shared.AppConfig.HKUST_LOCATION
import com.foobarust.domain.models.common.GeolocationPoint
import com.foobarust.domain.models.map.TravelMode
import com.foobarust.domain.models.order.*
import com.foobarust.domain.models.user.UserDelivery
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.maps.GetDirectionsParameters
import com.foobarust.domain.usecases.maps.GetDirectionsUseCase
import com.foobarust.domain.usecases.order.GetOrderDetailUseCase
import com.foobarust.domain.usecases.user.GetDelivererProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 2/1/21
 */

private const val ORDER_STATE_LIST_VISIBLE_INDEX = 1

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val getOrderDetailUseCase: GetOrderDetailUseCase,
    private val getDirectionsUseCase: GetDirectionsUseCase,
    private val getDelivererProfileUseCase: GetDelivererProfileUseCase,
    private val orderStateUtil: OrderStateUtil,
    private val paymentMethodUtil: PaymentMethodUtil
) : ViewModel() {

    private val _orderId = ConflatedBroadcastChannel<String>()

    private val _orderDetail = MutableStateFlow<OrderDetail?>(null)
    val orderDetail: StateFlow<OrderDetail?> = _orderDetail.asStateFlow()

    private val _orderDetailUiState = MutableStateFlow<OrderDetailUiState>(OrderDetailUiState.Loading)
    val orderDetailUiState: StateFlow<OrderDetailUiState> = _orderDetailUiState.asStateFlow()

    private val _orderDetailListModels = MutableStateFlow<List<OrderDetailListModel>>(emptyList())
    val orderDetailListModels: StateFlow<List<OrderDetailListModel>> = _orderDetailListModels
        .asStateFlow()

    private val _delivererProfile = MutableStateFlow<UserDelivery?>(null)
    val delivererProfile: StateFlow<UserDelivery?> = _delivererProfile.asStateFlow()

    private val _delivererRoute = MutableStateFlow<List<GeolocationPoint>>(emptyList())
    val delivererRoute: StateFlow<List<GeolocationPoint>> = _delivererRoute.asStateFlow()

    val deliveryLocation: Flow<GeolocationPoint> = _orderDetail
        .filterNotNull()
        .filter { it.type == OrderType.ON_CAMPUS }
        .mapLatest { it.deliveryLocation.locationPoint }

    val delivererMarkerInfo: Flow<DelivererMarkerInfo?> = _orderDetail
        .filterNotNull()
        .filter { it.type == OrderType.OFF_CAMPUS }
        .mapLatest { it.delivererLocation }
        .filterNotNull()
        .combine(_delivererProfile.filterNotNull()) { location, profile ->
            DelivererMarkerInfo(location, profile)
        }

    // Show map and collapse bottom sheet
    val showMap: Flow<Boolean?> = _orderDetail
        .filterNotNull()
        .mapLatest { orderDetail ->
            orderDetail.state !in orderCompletedStates
        }

    private var lastOrderStateItemPosition = 0

    private val orderCompletedStates = listOf(
        OrderState.DELIVERED,
        OrderState.ARCHIVED,
        OrderState.CANCELLED
    )

    init {
        // Fetch order detail
        viewModelScope.launch {
            _orderId.asFlow().flatMapLatest {
                getOrderDetailUseCase(it)
            }.collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        // Retrieve order detail
                        val orderDetail = result.data
                        _orderDetail.value = orderDetail

                        // Update ui state
                        _orderDetailUiState.value = OrderDetailUiState.Success

                        // Build detail list
                        val orderStateListModels = buildOrderStateListModels(orderDetail)
                        val orderInfoListModels = buildOrderInfoListModels(orderDetail)
                        _orderDetailListModels.value = orderStateListModels + orderInfoListModels

                        // Set last state item position
                        lastOrderStateItemPosition = ORDER_STATE_LIST_VISIBLE_INDEX
                    }
                    is Resource.Error -> {
                        _orderDetailUiState.value = OrderDetailUiState.Error(result.message)
                    }
                    is Resource.Loading -> {
                        _orderDetailUiState.value = OrderDetailUiState.Loading
                    }
                }
            }
        }

        // Fetch deliverer route
        viewModelScope.launch {
            _orderDetail.filterNotNull()
                .filter { it.type == OrderType.OFF_CAMPUS }
                .distinctUntilChangedBy { it.delivererLocation }
                .flatMapLatest { orderDetail ->
                    val delivererLocation = orderDetail.delivererLocation
                    if (delivererLocation != null) {
                        val params = GetDirectionsParameters(
                            currentLocation = delivererLocation,
                            destination = HKUST_LOCATION,
                            travelMode = TravelMode.DRIVING
                        )
                        getDirectionsUseCase(params)
                    } else {
                        emptyFlow()
                    }
                }.collectLatest {
                    when (it) {
                        is Resource.Success -> _delivererRoute.value = it.data
                        is Resource.Error -> Unit
                        is Resource.Loading -> Unit
                    }
                }
        }

        // Fetch deliverer profile
        viewModelScope.launch {
            _orderDetail.filterNotNull()
                .filter { it.type == OrderType.OFF_CAMPUS }
                .distinctUntilChangedBy { it.delivererId }
                .flatMapLatest { orderDetail ->
                    val delivererId = orderDetail.delivererId
                    if (delivererId != null) {
                        getDelivererProfileUseCase(delivererId)
                    } else {
                        emptyFlow()
                    }
                }.collectLatest {
                    when (it) {
                        is Resource.Success -> _delivererProfile.value = it.data
                        is Resource.Error -> Unit
                        is Resource.Loading -> Unit
                    }
                }
        }
    }

    fun onFetchOrderDetail(orderId: String) {
        _orderId.offer(orderId)
    }

    fun getLastOrderStateItemPosition(): Int = lastOrderStateItemPosition

    private fun buildOrderStateListModels(
        orderDetail: OrderDetail
    ): List<OrderDetailListModel> {
        val currentOrderState = orderDetail.state

        if (currentOrderState == OrderState.ARCHIVED) {
            return emptyList()
        } else {
            return buildList {
                add(OrderDetailHeaderItemModel(
                    orderType = orderDetail.type,
                    deliveryAddress = orderDetail.getNormalizedDeliveryAddress()
                ))

                // Current state
                add(OrderDetailStateItemModel(
                    currentOrderState = currentOrderState,
                    listOrderState = currentOrderState,
                    listStateTitle = orderStateUtil.getOrderStateTitle(currentOrderState),
                    listStateDescription = orderStateUtil.getOrderStateDescription(currentOrderState)
                ))

                // Elapsed state
                if (currentOrderState !in orderCompletedStates) {
                    addAll(getElapsedOrderStateListModels(currentOrderState))
                }
            }
        }
    }

    private fun buildOrderInfoListModels(orderDetail: OrderDetail): List<OrderDetailListModel> {
        return buildList {
            add(OrderDetailInfoItemModel(
                orderIdentifier = orderDetail.identifier,
                orderTitle = orderDetail.getNormalizedTitle(),
                orderCreatedDate = orderDetail.createdAt,
                orderTotalCost = orderDetail.totalCost,
                orderMessage = orderDetail.message,
                orderItemImageUrl = orderDetail.imageUrl
            ))

            addAll(orderDetail.orderItems.map {
                OrderDetailPurchaseItemModel(
                    orderItemId = it.itemId,
                    orderItemTitle = it.getNormalizedTitle(),
                    orderItemAmounts = it.amounts,
                    orderItemTotalPrice = it.totalPrice,
                    orderItemImageUrl = it.itemImageUrl
                )
            })

            add(OrderDetailCostItemModel(
                orderSubtotal = orderDetail.subtotalCost,
                orderDeliveryCost = orderDetail.deliveryCost
            ))

            add(OrderDetailPaymentItemModel(
                paymentMethodItem = paymentMethodUtil.getPaymentMethodItem(
                    orderDetail.paymentMethod
                )
            ))

            add(OrderDetailActionsItemModel)
        }
    }

    private fun getElapsedOrderStateListModels(currentOrderState: OrderState): List<OrderDetailListModel> {
        return OrderState.values()
            .filterNot { it == OrderState.CANCELLED }
            .filter { it.precedence < currentOrderState.precedence }
            .sortedByDescending { it.precedence }
            .map {
                OrderDetailStateItemModel(
                    currentOrderState = currentOrderState,
                    listOrderState = it,
                    listStateTitle = orderStateUtil.getOrderStateTitle(it),
                    listStateDescription = orderStateUtil.getOrderStateDescription(it)
                )
            }
    }
}

sealed class OrderDetailUiState {
    object Success : OrderDetailUiState()
    data class Error(val message: String?) : OrderDetailUiState()
    object Loading : OrderDetailUiState()
}

data class DelivererMarkerInfo(
    val locationPoint: GeolocationPoint,
    val userDelivery: UserDelivery
)