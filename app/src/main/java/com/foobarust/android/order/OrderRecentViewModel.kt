package com.foobarust.android.order

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.order.OrderRecentListModel.OrderRecentActiveItemModel
import com.foobarust.android.order.OrderRecentListModel.OrderRecentEmptyItemModel
import com.foobarust.domain.models.order.*
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.order.GetRecentOrdersUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 1/29/21
 */

@HiltViewModel
class OrderRecentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getRecentOrdersUseCase: GetRecentOrdersUseCase,
    private val orderStateUtil: OrderStateUtil
) : ViewModel() {

    private val _orderRecentListModels = MutableStateFlow<List<OrderRecentListModel>>(emptyList())
    val orderRecentListModels: LiveData<List<OrderRecentListModel>> = _orderRecentListModels
        .asLiveData(viewModelScope.coroutineContext)

    private val _orderRecentUiState = MutableStateFlow<OrderRecentUiState>(OrderRecentUiState.Loading)
    val orderRecentUiState: LiveData<OrderRecentUiState> = _orderRecentUiState
        .asLiveData(viewModelScope.coroutineContext)

    private val _finishSwipeRefresh = ConflatedBroadcastChannel(Unit)
    val finishSwipeRefresh: LiveData<Unit> = _finishSwipeRefresh
        .asFlow()
        .asLiveData(viewModelScope.coroutineContext)

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
                        _orderRecentListModels.value = buildOrderRecentListModels(orderItems = it.data)
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
            return listOf(
                OrderRecentEmptyItemModel(
                    drawableRes = R.drawable.undraw_receipt,
                    emptyMessage = context.getString(R.string.order_recent_empty_item_title)
                )
            )
        }

        return orderItems.map { orderBasic ->
            val orderImageTitle = when (orderBasic.type) {
                OrderType.ON_CAMPUS -> orderBasic.getNormalizedSellerName()
                OrderType.OFF_CAMPUS -> "${orderBasic.getNormalizedSellerName()}\n${orderBasic.getNormalizedTitle()}"
            }

            OrderRecentActiveItemModel(
                orderId = orderBasic.id,
                orderImageTitle = orderImageTitle,
                orderStateTitle = context.getString(
                    R.string.order_item_identifier_title,
                    orderBasic.identifier,
                    orderStateUtil.getOrderStateTitle(orderBasic.state)
                ),
                orderDeliveryAddress = orderBasic.getNormalizedDeliveryAddress(),
                orderImageUrl = orderBasic.imageUrl,
                orderState = orderBasic.state,
                orderCreatedAt = context.getString(
                    R.string.order_item_created_at,
                    orderBasic.getCreatedAtString()
                ),
                orderTotalCost = orderBasic.totalCost
            )
        }
    }
}

sealed class OrderRecentUiState {
    object Success : OrderRecentUiState()
    data class Error(val message: String?) : OrderRecentUiState()
    object Loading : OrderRecentUiState()
}