package com.foobarust.android.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.UiState
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.checkout.PlaceOrderParameters
import com.foobarust.domain.usecases.checkout.PlaceOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 1/26/21
 */

@HiltViewModel
class OrderPlacingViewModel @Inject constructor(
    private val placeOrderUseCase: PlaceOrderUseCase
) : BaseViewModel() {

    private val _placeOrderState = MutableStateFlow(PlaceOrderState.IDLE)
    val placeOrderState: LiveData<PlaceOrderState> = _placeOrderState
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
                    showToastMessage(it.data.toString())
                    _placeOrderState.value = PlaceOrderState.SUCCESS
                    blockReturn = false
                }
                is Resource.Error -> {
                    setUiState(UiState.Error(it.message))
                    _placeOrderState.value = PlaceOrderState.FAILURE
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

enum class PlaceOrderState {
    IDLE,
    SUCCESS,
    FAILURE
}