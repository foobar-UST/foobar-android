package com.foobarust.android.order

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.order.OrderHistoryListModel.*
import com.foobarust.android.order.OrderRecentListModel.*
import com.foobarust.domain.models.order.OrderBasic
import com.foobarust.domain.models.order.OrderState
import com.foobarust.domain.models.order.getCreatedAtString
import com.foobarust.domain.models.order.getNormalizedTitle
import com.foobarust.domain.usecases.order.GetArchivedOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

    val recentListModels: Flow<PagingData<OrderHistoryListModel>> = getArchivedOrdersUseCase(Unit)
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
        return  if (orderBasic.state == OrderState.DELIVERED) {
            OrderHistoryDeliveredItemModel(
                orderId = orderBasic.id,
                orderIdentifierTitle = context.getString(
                    R.string.order_recent_item_identifier_title,
                    orderBasic.identifier,
                    orderStateUtil.getOrderStateTitle(orderBasic.state)
                ),
                orderTitle = orderBasic.getNormalizedTitle(),
                orderImageUrl = orderBasic.imageUrl
            )
        } else {
            OrderHistoryArchivedItemModel(
                orderId = orderBasic.id,
                orderIdentifierTitle = context.getString(
                    R.string.order_history_item_identifier_title,
                    orderBasic.identifier,
                    orderBasic.getNormalizedTitle()
                ),
                orderDeliveryDate = context.getString(
                    R.string.order_history_ordered_date,
                    orderBasic.getCreatedAtString()
                ),
                orderTotalCost = context.getString(
                    R.string.order_history_total_cost,
                    orderBasic.totalCost
                ),
                orderImageUrl = orderBasic.imageUrl
            )
        }
    }

    private fun insertSeparators(
        before: OrderHistoryListModel?,
        after: OrderHistoryListModel?
    ): OrderHistoryListModel? {
        return if (before == null && after == null) {
            OrderHistoryEmptyItemModel(
                emptyTitle = context.getString(R.string.order_history_empty_item_title)
            )
        } else if (before is OrderHistoryDeliveredItemModel && after is OrderHistoryArchivedItemModel) {
            OrderHistorySubtitleItemModel(
                subtitle = context.getString(R.string.order_recent_item_subtitle_archived)
            )
        } else {
            null
        }
    }
}