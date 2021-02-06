package com.foobarust.android.order

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.android.R
import com.foobarust.android.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _navigateToOrderDetail = SingleLiveEvent<String>()
    val navigateToOrderDetail: LiveData<String>
        get() = _navigateToOrderDetail

    private val _scrollToTop = MutableSharedFlow<Int>()
    val scrollToTop: SharedFlow<Int> = _scrollToTop.asSharedFlow()

    var currentTabPage: Int = 0

    fun onScrollToTop() = viewModelScope.launch {
        _scrollToTop.emit(currentTabPage)
    }

    fun onNavigateToOrderDetail(orderId: String) {
        _navigateToOrderDetail.value = orderId
    }
}