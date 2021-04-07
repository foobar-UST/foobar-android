package com.foobarust.android.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foobarust.domain.models.order.OrderState
import com.foobarust.domain.states.getSuccessDataOr
import com.foobarust.domain.usecases.order.GetOrderDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Created by kevin on 4/7/21
 */

@HiltViewModel
class VerifyOrderViewModel @Inject constructor(
    getOrderDetailUseCase: GetOrderDetailUseCase
) : ViewModel() {

    private val _orderId = MutableStateFlow<String?>(null)

    private val verifiedStates = listOf(
        OrderState.DELIVERED,
        OrderState.ARCHIVED,
        OrderState.CANCELLED
    )

    val orderVerified: StateFlow<Boolean> = _orderId
        .filterNotNull()
        .flatMapLatest {
            getOrderDetailUseCase(it)
        }
        .mapLatest {
            val orderState = it.getSuccessDataOr(null)?.state
            orderState in verifiedStates
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    fun onObserveOrderVerified(orderId: String) {
        _orderId.value = orderId
    }
}