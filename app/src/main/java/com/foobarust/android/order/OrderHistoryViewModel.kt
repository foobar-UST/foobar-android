package com.foobarust.android.order

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.foobarust.android.R
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.OnSwipeRefreshListener
import com.foobarust.android.order.OrderHistoryListModel.*
import com.foobarust.android.order.OrderRecentListModel.*
import com.foobarust.android.utils.asUiState
import com.foobarust.domain.models.order.getCreatedAtString
import com.foobarust.domain.models.order.getNormalizedTitle
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
    getArchivedOrdersUseCase: GetArchivedOrdersUseCase
) : BaseViewModel(), OnSwipeRefreshListener {

    private val _fetchOrderHistory = ConflatedBroadcastChannel(Unit)

    val recentListModels: Flow<PagingData<OrderHistoryListModel>> = _fetchOrderHistory
        .asFlow()
        .flatMapLatest { getArchivedOrdersUseCase(Unit) }
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

    private val _isSwipeRefreshing = MutableStateFlow(false)
    val isSwipeRefreshing: LiveData<Boolean> = _isSwipeRefreshing
        .asLiveData(viewModelScope.coroutineContext)

    override fun onSwipeRefresh() {
        _isSwipeRefreshing.value = true
    }

    fun onPagingLoadStateChanged(loadState: LoadState) {
        if (_isSwipeRefreshing.value && loadState is LoadState.Loading) {
            return
        }
        if (loadState is LoadState.NotLoading || loadState is LoadState.Error) {
            _isSwipeRefreshing.value = false
        }

        setUiState(loadState.asUiState())
    }
}