package com.foobarust.android.order

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.order.OrderHistoryListModel.*
import com.foobarust.android.order.OrderRecentListModel.*
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
    getArchivedOrdersUseCase: GetArchivedOrdersUseCase
) : ViewModel() {

    val recentListModels: Flow<PagingData<OrderHistoryListModel>> = getArchivedOrdersUseCase(Unit)
        .map { pagingData ->
            pagingData.map {
                OrderHistoryItemModel(
                    orderId = it.id,
                    orderIdentifierTitle = context.getString(
                        R.string.order_history_item_identifier_title,
                        it.identifier,
                        it.getNormalizedTitle()
                    ),
                    orderDeliveryDate = context.getString(
                        R.string.order_history_ordered_date,
                        it.getCreatedAtString()
                    ),
                    orderTotalCost = context.getString(
                        R.string.order_history_total_cost,
                        it.totalCost
                    ),
                    orderImageUrl = it.imageUrl
                )
            }
        }
        .map { pagingData ->
            // Insert empty view
            pagingData.insertSeparators { before, after ->
                return@insertSeparators if (before == null && after == null) {
                   OrderHistoryEmptyItemModel(
                       emptyTitle = context.getString(R.string.order_history_empty_item_title)
                   )
                } else {
                    null
                }
            }
        }
        .cachedIn(viewModelScope)
}