package com.foobarust.android.order

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.checkout.PaymentMethodUtil
import com.foobarust.android.order.OrderDetailListModel.*
import com.foobarust.domain.models.order.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.order.GetOrderDetailUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 2/1/21
 */

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getOrderDetailUseCase: GetOrderDetailUseCase,
    private val orderStateUtil: OrderStateUtil,
    private val paymentMethodUtil: PaymentMethodUtil
) : ViewModel() {

    private val _orderDetail = MutableStateFlow<OrderDetail?>(null)

    private val _orderDetailListModels = MutableStateFlow<List<OrderDetailListModel>>(emptyList())
    val orderDetailListModels: StateFlow<List<OrderDetailListModel>> = _orderDetailListModels
        .asStateFlow()

    private val _orderDetailUiState = MutableStateFlow<OrderDetailUiState>(OrderDetailUiState.Loading)
    val orderDetailUiState: StateFlow<OrderDetailUiState> = _orderDetailUiState
        .asStateFlow()

    private val _bottomSheetExpanded = MutableStateFlow<Boolean?>(null)
    val bottomSheetExpanded: StateFlow<Boolean?> = _bottomSheetExpanded
        .asStateFlow()

    // Argument: seller id
    private val _navigateToSellerMisc = Channel<String>()
    val navigateToSellerMisc: Flow<String> = _navigateToSellerMisc.receiveAsFlow()

    private var lastOrderStateItemPosition = 0

    private var fetchOrderDetailJob: Job? = null

    fun onFetchOrderDetail(orderId: String) {
        fetchOrderDetailJob?.cancelIfActive()
        fetchOrderDetailJob = viewModelScope.launch {
            getOrderDetailUseCase(orderId).collect {
                when (it) {
                    is Resource.Success -> {
                        val orderDetail = it.data
                        _orderDetail.value = orderDetail

                        val orderStateListModels = buildOrderStateListModels(orderDetail)
                        val orderInfoListModels = buildOrderInfoListModels(orderDetail)

                        // Set show map
                        _bottomSheetExpanded.value = orderDetail.state in listOf(
                            OrderState.DELIVERED,
                            OrderState.ARCHIVED,
                            OrderState.CANCELLED
                        )

                        // Set last state item position
                        lastOrderStateItemPosition = orderStateListModels.lastIndex

                        _orderDetailListModels.value = orderStateListModels + orderInfoListModels
                        _orderDetailUiState.value = OrderDetailUiState.Success
                    }
                    is Resource.Error -> {
                        _orderDetailUiState.value = OrderDetailUiState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        _orderDetailUiState.value = OrderDetailUiState.Loading
                    }
                }
            }
        }
    }

    fun getLastOrderStateItemPosition(): Int = lastOrderStateItemPosition

    fun onNavigateToSellerMisc() {
        _orderDetail.value?.let {
            _navigateToSellerMisc.offer(it.sellerId)
        }
    }

    private fun buildOrderStateListModels(
        orderDetail: OrderDetail
    ): List<OrderDetailListModel> {
        val currentOrderState = orderDetail.state

        if (currentOrderState == OrderState.ARCHIVED) {
            return emptyList()
        } else {
            return buildList {
                add(OrderDetailHeaderItemModel(
                    deliveryAddress = orderDetail.getNormalizedDeliveryAddress(),
                    deliveryAddressTitle = if (orderDetail.type == OrderType.ON_CAMPUS) {
                        context.getString(
                            R.string.order_detail_header_item_delivery_address_title_on_campus
                        )
                    } else {
                        context.getString(
                            R.string.order_detail_header_item_delivery_address_title_off_campus
                        )
                    }
                ))

                addAll(getElapsedOrderStateListModels(currentOrderState))

                add(OrderDetailStateItemModel(
                    currentOrderState = currentOrderState,
                    listOrderState = currentOrderState,
                    listStateTitle = orderStateUtil.getOrderStateTitle(currentOrderState),
                    listStateDescription = orderStateUtil.getOrderStateDescription(currentOrderState)
                ))
            }
        }
    }

    private fun buildOrderInfoListModels(orderDetail: OrderDetail): List<OrderDetailListModel> {
        return buildList {
            add(OrderDetailInfoItemModel(
                orderIdentifierTitle = context.getString(
                    R.string.order_detail_info_item_identifier_title,
                    orderDetail.identifier
                ),
                orderTitle = orderDetail.getNormalizedTitle(),
                orderCreatedDate = context.getString(
                    R.string.order_detail_info_item_created_at,
                    orderDetail.getCreatedAtString()
                ),
                orderTotalCost = context.getString(
                    R.string.order_detail_info_item_total_cost,
                    orderDetail.totalCost
                ),
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
            .sortedBy { it.precedence }
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