package com.foobarust.android.order

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.order.OrderHistoryListModel.*
import com.foobarust.android.order.OrderRecentListModel.*
import com.foobarust.domain.models.order.*
import com.foobarust.domain.usecases.order.GetArchivedOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by kevin on 1/30/21
 */

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getArchivedOrdersUseCase: GetArchivedOrdersUseCase,
    private val orderStateUtil: OrderStateUtil
) : ViewModel() {

    private val _refreshList = ConflatedBroadcastChannel(Unit)

    val orderHistoryListModels: Flow<PagingData<OrderHistoryListModel>> = _refreshList
        .asFlow()
        .flatMapLatest { getArchivedOrdersUseCase(Unit) }
        .map { pagingData ->
            pagingData.map { getOrderHistoryListModel(it) }
        }
        .map { pagingData ->
            pagingData.insertSeparators { before, after ->
                return@insertSeparators insertSeparators(before, after)
            }
        }
        .cachedIn(viewModelScope)

    private fun getOrderHistoryListModel(orderBasic: OrderBasic): OrderHistoryListModel {
        val orderImageTitle = when (orderBasic.type) {
            OrderType.ON_CAMPUS -> orderBasic.getNormalizedSellerName()
            OrderType.OFF_CAMPUS -> "${orderBasic.getNormalizedSellerName()}\n${orderBasic.getNormalizedTitle()}"
        }

        return OrderHistoryItemModel(
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

    private fun insertSeparators(
        before: OrderHistoryListModel?,
        after: OrderHistoryListModel?
    ): OrderHistoryListModel? {
        return when {
            checkInsertEmptyItemModel(before, after) -> {
                OrderHistoryEmptyItemModel(
                    drawableRes = R.drawable.undraw_receipt,
                    emptyMessage = context.getString(R.string.order_history_empty_message)
                )
            }
            else -> null
        }
    }

    private fun checkInsertEmptyItemModel(
        before: OrderHistoryListModel?,
        after: OrderHistoryListModel?
    ): Boolean {
        return before == null && after == null
    }
}