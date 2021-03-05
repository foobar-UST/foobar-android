package com.foobarust.android.order

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 1/29/21
 */

@HiltViewModel
class OrderViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    val orderPages: List<OrderPage> = listOf(
        OrderPage(
            title = context.getString(R.string.order_tab_recent),
            fragment = { OrderRecentFragment() }
        ),
        OrderPage(
            title = context.getString(R.string.order_tab_history),
            fragment = { OrderHistoryFragment() }
        )
    )

    private val _navigateToOrderDetail = Channel<String>()
    val navigateToOrderDetail: Flow<String> = _navigateToOrderDetail.receiveAsFlow()

    private val _scrollToTop = MutableSharedFlow<Int>()
    val scrollToTop: SharedFlow<Int> = _scrollToTop.asSharedFlow()

    // Argument: order id
    private val _navigateToRating = Channel<String>()
    val navigateToRating: Flow<String> = _navigateToRating.receiveAsFlow()

    private val _refreshHistoryList = Channel<Unit>()
    val refreshHistoryList: Flow<Unit> = _refreshHistoryList.receiveAsFlow()

    var currentTabPage: Int = 0

    fun onRefreshHistoryList() {
        _refreshHistoryList.offer(Unit)
    }

    fun onScrollToTop() = viewModelScope.launch {
        _scrollToTop.emit(currentTabPage)
    }

    fun onNavigateToOrderDetail(orderId: String) {
        _navigateToOrderDetail.offer(orderId)
    }

    fun onNavigateToRating(orderId: String) {
        _navigateToRating.offer(orderId)
    }
}