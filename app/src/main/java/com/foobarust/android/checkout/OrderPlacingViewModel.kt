package com.foobarust.android.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.UiState
import com.foobarust.domain.models.checkout.PlaceOrderResult
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.checkout.PlaceOrderParameters
import com.foobarust.domain.usecases.checkout.PlaceOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 1/26/21
 */

@HiltViewModel
class OrderPlacingViewModel @Inject constructor(
    private val placeOrderUseCase: PlaceOrderUseCase
) : BaseViewModel() {

    private val _placeOrderState = MutableStateFlow<PlaceOrderState>(PlaceOrderState.Idle)
    val placeOrderState: LiveData<PlaceOrderState> = _placeOrderState
        .asLiveData(viewModelScope.coroutineContext)

    val navigateToOrderResult: LiveData<OrderResultProperty?> = _placeOrderState
        .map {
            when (it) {
                is PlaceOrderState.Idle -> null
                is PlaceOrderState.Success -> OrderResultProperty(
                    orderId = it.result.orderId,
                    orderIdentifier = it.result.orderIdentifier
                )
                is PlaceOrderState.Failure -> OrderResultProperty(
                    errorMessage = it.message
                )
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    // Prevent user using back button during transaction
    private var blockReturn: Boolean = false

    fun onStartPlacingOrder(orderMessage: String?, paymentMethodIdentifier: String) = viewModelScope.launch {
        val params = PlaceOrderParameters(
            orderMessage = orderMessage,
            paymentMethodIdentifier = paymentMethodIdentifier
        )
        placeOrderUseCase(params).collect {
            when (it) {
                is Resource.Success -> {
                    setUiState(UiState.Success)
                    _placeOrderState.value = PlaceOrderState.Success(it.data)
                    blockReturn = false
                }
                is Resource.Error -> {
                    setUiState(UiState.Error(it.message))
                    _placeOrderState.value = PlaceOrderState.Failure(it.message)
                    blockReturn = false
                }
                is Resource.Loading -> {
                    setUiState(UiState.Loading)
                    blockReturn = true
                }
            }
        }
    }
}

sealed class PlaceOrderState {
    object Idle : PlaceOrderState()

    data class Success(val result: PlaceOrderResult) : PlaceOrderState()

    data class Failure(val message: String?) : PlaceOrderState()
}