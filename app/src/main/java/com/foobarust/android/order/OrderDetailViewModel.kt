package com.foobarust.android.order

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.checkout.PaymentMethodUtil
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.UiState
import com.foobarust.android.order.OrderDetailListModel.*
import com.foobarust.domain.models.order.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.order.GetOrderDetailUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
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
) : BaseViewModel() {

    private val _orderStateListModels = MutableStateFlow<List<OrderDetailListModel>>(emptyList())
    private val _orderInfoListModels = MutableStateFlow<List<OrderDetailListModel>>(emptyList())

    private val _showMapFragment = MutableStateFlow<Boolean?>(null)
    val showMapFragment: LiveData<Boolean?> = _showMapFragment
        .asLiveData(viewModelScope.coroutineContext)

    val orderDetailListModels: LiveData<List<OrderDetailListModel>> = _orderStateListModels
        .combine(_orderInfoListModels) { orderStateListModels, orderInfoListModels ->
            orderStateListModels + orderInfoListModels
        }
        .asLiveData(viewModelScope.coroutineContext)

    val lastStateItemIndex: LiveData<Int> = _orderStateListModels
        .map { it.lastIndex }
        .asLiveData(viewModelScope.coroutineContext)

    private var fetchOrderDetailJob: Job? = null

    fun onFetchOrderDetail(orderId: String) {
        fetchOrderDetailJob?.cancelIfActive()
        fetchOrderDetailJob = viewModelScope.launch {
            getOrderDetailUseCase(orderId).collect {
                when (it) {
                    is Resource.Success -> {
                        setUiState(UiState.Success)
                        setShowMapFragment(orderDetail = it.data)
                        buildOrderStateListModels(orderDetail = it.data)
                        buildOrderInfoListModels(orderDetail = it.data)
                    }
                    is Resource.Error -> {
                        setUiState(UiState.Error(it.message))
                        clearOrderStateListModels()
                    }
                    is Resource.Loading -> {
                        setUiState(UiState.Loading)
                        clearOrderStateListModels()
                    }
                }
            }
        }
    }

    fun getLastOrderStateItemPosition(): Int = _orderStateListModels.value.lastIndex

    private fun setShowMapFragment(orderDetail: OrderDetail) {
        _showMapFragment.value = orderDetail.state !in listOf(
            OrderState.DELIVERED,
            OrderState.ARCHIVED,
            OrderState.CANCELLED
        )
    }

    private fun buildOrderStateListModels(orderDetail: OrderDetail) {
        val currentOrderState = orderDetail.state
        _orderStateListModels.value = if (currentOrderState == OrderState.ARCHIVED) {
            emptyList()
        } else {
            buildList {
                add(
                    OrderDetailHeaderItemModel(
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
                    )
                )
                addAll(getElapsedOrderStateListModels(currentOrderState))
                add(
                    OrderDetailStateItemModel(
                        currentOrderState = currentOrderState,
                        listOrderState = currentOrderState,
                        listStateTitle = orderStateUtil.getOrderStateTitle(currentOrderState),
                        listStateDescription = orderStateUtil.getOrderStateDescription(currentOrderState)
                    )
                )
            }

        }
    }

    private fun buildOrderInfoListModels(orderDetail: OrderDetail) {
        _orderInfoListModels.value = buildList {
            add(
                OrderDetailInfoItemModel(
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
                )
            )
            addAll(
                orderDetail.orderItems.map {
                    OrderDetailPurchaseItemModel(
                        orderItemId = it.itemId,
                        orderItemTitle = it.getNormalizedTitle(),
                        orderItemAmounts = it.amounts,
                        orderItemTotalPrice = it.totalPrice,
                        orderItemImageUrl = it.itemImageUrl
                    )
                }
            )
            add(
                OrderDetailCostItemModel(
                    orderSubtotal = orderDetail.subtotalCost,
                    orderDeliveryCost = orderDetail.deliveryCost
                )
            )
            add(
                OrderDetailPaymentItemModel(
                    paymentMethodItem = paymentMethodUtil.getPaymentMethodItem(
                        orderDetail.paymentMethod
                    )
                )
            )
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

    private fun clearOrderStateListModels() {
        _orderStateListModels.value = emptyList()
        _orderInfoListModels.value = emptyList()
    }
}

@Parcelize
data class OrderDetailProperty(
    val orderId: String
) : Parcelable