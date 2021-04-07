package com.foobarust.android.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.foobarust.android.order.OrderHistoryListModel.*
import com.foobarust.android.order.OrderRecentListModel.*
import com.foobarust.domain.models.order.*
import com.foobarust.domain.usecases.order.GetArchivedOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by kevin on 1/30/21
 */

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
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
        return OrderHistoryItemModel(
            orderId = orderBasic.id,
            orderTitle = orderBasic.getNormalizedTitle(),
            orderType = orderBasic.type,
            orderIdentifier = orderBasic.identifier,
            orderState = orderBasic.state,
            orderStateTitle = orderStateUtil.getOrderStateTitle(orderBasic.state),
            orderDeliveryAddress = orderBasic.getNormalizedDeliveryAddress(),
            orderImageUrl = orderBasic.imageUrl,
            orderCreatedAt = orderBasic.createdAt,
            orderTotalCost = orderBasic.totalCost,
            sellerName = orderBasic.getNormalizedSellerName()
        )
    }

    private fun insertSeparators(
        before: OrderHistoryListModel?,
        after: OrderHistoryListModel?
    ): OrderHistoryListModel? {
        return when {
            checkInsertEmptyItemModel(before, after) -> OrderHistoryEmptyItemModel
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