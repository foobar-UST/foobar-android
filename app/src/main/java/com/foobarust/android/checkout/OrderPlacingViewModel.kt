package com.foobarust.android.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.domain.models.checkout.PlaceOrderResult
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.checkout.PlaceOrderParameters
import com.foobarust.domain.usecases.checkout.PlaceOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 1/26/21
 */

@HiltViewModel
class OrderPlacingViewModel @Inject constructor(
    private val placeOrderUseCase: PlaceOrderUseCase
) : ViewModel() {

    private val _placeOrderUiState = MutableStateFlow<PlaceOrderUiState>(PlaceOrderUiState.Idle)
    val placeOrderUiState: LiveData<PlaceOrderUiState> = _placeOrderUiState
        .asLiveData(viewModelScope.coroutineContext)

    val navigateToOrderResult: LiveData<OrderResultProperty> = _placeOrderUiState
        .map {
            when (it) {
                is PlaceOrderUiState.Success -> OrderResultProperty(
                    orderId = it.result.orderId,
                    orderIdentifier = it.result.orderIdentifier
                )
                is PlaceOrderUiState.Failure -> OrderResultProperty(
                    errorMessage = it.message
                )
                else -> null
            }
        }
        .filterNotNull()
        .asLiveData(viewModelScope.coroutineContext)

    fun onStartPlacingOrder(orderMessage: String?, paymentMethodIdentifier: String) = viewModelScope.launch {
        val params = PlaceOrderParameters(
            orderMessage = orderMessage,
            paymentMethodIdentifier = paymentMethodIdentifier
        )

        placeOrderUseCase(params).collect {
            _placeOrderUiState.value = when (it) {
                is Resource.Success -> PlaceOrderUiState.Success(it.data)
                is Resource.Error -> PlaceOrderUiState.Failure(it.message)
                is Resource.Loading -> PlaceOrderUiState.Loading
            }
        }
    }

    fun isPlacingOrder(): Boolean = _placeOrderUiState.value == PlaceOrderUiState.Loading
}

sealed class PlaceOrderUiState {
    object Idle : PlaceOrderUiState()
    data class Success(val result: PlaceOrderResult) : PlaceOrderUiState()
    data class Failure(val message: String?) : PlaceOrderUiState()
    object Loading : PlaceOrderUiState()
}