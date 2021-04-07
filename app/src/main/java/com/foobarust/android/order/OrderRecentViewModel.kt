package com.foobarust.android.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.order.OrderRecentListModel.OrderRecentActiveItemModel
import com.foobarust.android.order.OrderRecentListModel.OrderRecentEmptyItemModel
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.models.order.getNormalizedDeliveryAddress
import com.foobarust.domain.models.order.getNormalizedSellerName
import com.foobarust.domain.models.order.getNormalizedTitle
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.order.GetRecentOrdersUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 1/29/21
 */

@HiltViewModel
class OrderRecentViewModel @Inject constructor(
    private val getRecentOrdersUseCase: GetRecentOrdersUseCase,
    private val orderStateUtil: OrderStateUtil
) : ViewModel() {

    private val _orderRecentListModels = MutableStateFlow<List<OrderRecentListModel>>(emptyList())
    val orderRecentListModels: StateFlow<List<OrderRecentListModel>> = _orderRecentListModels
        .asStateFlow()

    private val _orderRecentUiState = MutableStateFlow<OrderRecentUiState>(OrderRecentUiState.Loading)
    val orderRecentUiState: StateFlow<OrderRecentUiState> = _orderRecentUiState
        .asStateFlow()

    private val _finishSwipeRefresh = Channel<Unit>()
    val finishSwipeRefresh: Flow<Unit> = _finishSwipeRefresh.receiveAsFlow()

    private var fetchOrderItemsJob: Job? = null

    init {
        onFetchOrderItems()
    }

    fun onFetchOrderItems(isSwipeRefresh: Boolean = false) {
        fetchOrderItemsJob?.cancelIfActive()
        fetchOrderItemsJob = viewModelScope.launch {
            getRecentOrdersUseCase(Unit).collect {
                when (it) {
                    is Resource.Success -> {
                        _orderRecentListModels.value = buildOrderRecentListModels(it.data)
                        _orderRecentUiState.value = OrderRecentUiState.Success

                        if (isSwipeRefresh) {
                            _finishSwipeRefresh.offer(Unit)
                        }
                    }
                    is Resource.Error -> {
                        _orderRecentUiState.value = OrderRecentUiState.Error(it.message)

                        if (isSwipeRefresh) {
                            _finishSwipeRefresh.offer(Unit)
                        }
                    }
                    is Resource.Loading -> {
                        if (!isSwipeRefresh) {
                            _orderRecentUiState.value = OrderRecentUiState.Loading
                        } else {
                            _finishSwipeRefresh.offer(Unit)
                        }
                    }
                }
            }
        }
    }

    private fun buildOrderRecentListModels(orderItems: List<OrderBasic>): List<OrderRecentListModel> {
        if (orderItems.isEmpty()) {
            return listOf(OrderRecentEmptyItemModel)
        }

        return orderItems.map { orderBasic ->
            OrderRecentActiveItemModel(
                orderId = orderBasic.id,
                orderTitle = orderBasic.getNormalizedTitle(),
                orderType = orderBasic.type,
                orderIdentifier = orderBasic.identifier,
                orderState = orderBasic.state,
                orderStateTitle = orderStateUtil.getOrderStateTitle(orderBasic.state),
                orderDeliveryAddress = orderBasic.getNormalizedDeliveryAddress(),
                orderTotalCost = orderBasic.totalCost,
                orderImageUrl = orderBasic.imageUrl,
                orderCreatedAt = orderBasic.createdAt,
                sellerName = orderBasic.getNormalizedSellerName()
            )
        }
    }
}

sealed class OrderRecentUiState {
    object Success : OrderRecentUiState()
    data class Error(val message: String?) : OrderRecentUiState()
    object Loading : OrderRecentUiState()
}