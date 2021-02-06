package com.foobarust.android.order

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.UiState
import com.foobarust.android.order.OrderRecentListModel.*
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
) : BaseViewModel() {

    private val _orderRecentListModels = MutableStateFlow<List<OrderRecentListModel>>(emptyList())
    val recentListModels: LiveData<List<OrderRecentListModel>> = _orderRecentListModels
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
                        setUiState(UiState.Success)
                        _finishSwipeRefresh.offer(Unit)
                        _orderRecentListModels.value = buildOrderRecentListModels(
                            orderItems = it.data
                        )
                    }
                    is Resource.Error -> {
                        setUiState(UiState.Error(it.message))
                        _finishSwipeRefresh.offer(Unit)
                        _orderRecentListModels.value = buildOrderRecentListModels()
                    }
                    is Resource.Loading -> {
                        if (!isSwipeRefresh) {
                            setUiState(UiState.Loading)
                            _orderRecentListModels.value = emptyList()
                        } else {
                            _finishSwipeRefresh.offer(Unit)
                        }
                    }
                }
            }
        }
    }

    private fun buildOrderRecentListModels(
        orderItems: List<OrderBasic> = emptyList()
    ): List<OrderRecentListModel> {
        // No item placeholder
        if (orderItems.isEmpty()) {
            return listOf(
                OrderRecentEmptyItemModel(
                    emptyTitle = context.getString(R.string.order_recent_empty_item_title)
                )
            )
        }

        val result = mutableListOf<OrderRecentListModel>()
        orderItems.forEach { orderItem ->
            val orderIdentifierTitle = context.getString(
                R.string.order_recent_item_identifier_title,
                orderItem.identifier,
                orderStateUtil.getOrderStateTitle(orderItem.state)
            )
            val orderTitle = orderItem.getNormalizedTitle()

            if (orderItem.state == OrderState.DELIVERED) {
                if (result.isEmpty() || result.last() is OrderRecentActiveItemModel) {
                    result.add(
                        OrderRecentSubtitleItemModel(
                            subtitle = context.getString(R.string.order_recent_item_subtitle_delivered)
                        )
                    )
                }

                result.add(
                    OrderRecentDeliveredItemModel(
                        orderId = orderItem.id,
                        orderIdentifierTitle = orderIdentifierTitle,
                        orderTitle = orderTitle,
                        orderImageUrl = orderItem.imageUrl
                    )
                )
            } else {
                result.add(
                    OrderRecentActiveItemModel(
                        orderId = orderItem.id,
                        orderIdentifierTitle = orderIdentifierTitle,
                        orderTitle = orderTitle,
                        orderDeliveryAddress = orderItem.getNormalizedDeliveryAddress(),
                        orderImageUrl = orderItem.imageUrl,
                        orderState = orderItem.state,
                        orderUpdatedAt = context.getString(
                            R.string.order_recent_active_item_last_updated,
                            orderItem.getUpdatedAtString()
                        )
                    )
                )
            }
        }

        return result
    }
}