package com.foobarust.android.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.shared.BaseViewModel
import com.foobarust.domain.models.checkout.PaymentMethod
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.checkout.GetPaymentMethodsUseCase
import com.foobarust.domain.utils.cancelIfActive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kevin on 1/9/21
 */

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase,
    private val paymentMethodUtil: PaymentMethodUtil
) : BaseViewModel() {

    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())

    private val _paymentItemModels = MutableStateFlow<List<PaymentMethodItemModel>>(emptyList())
    val paymentItemModels: LiveData<List<PaymentMethodItemModel>> = _paymentItemModels
        .asLiveData(viewModelScope.coroutineContext)

    private val _paymentUiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
    val paymentUiState: LiveData<PaymentUiState> = _paymentUiState
        .asLiveData(viewModelScope.coroutineContext)

    private val _finishSwipeRefresh = Channel<Unit>()
    val finishSwipeRefresh: Flow<Unit> = _finishSwipeRefresh.receiveAsFlow()

    private var fetchPaymentMethodsJob: Job? = null

    init {
        onFetchPaymentMethods()

        // Build payment methods list
        viewModelScope.launch {
            _paymentMethods.combine(_paymentUiState) { paymentMethods, uiState ->
                val selectedIdentifier = (uiState as? PaymentUiState.Ready)?.identifier
                buildPaymentMethodItemModelsList(
                    paymentMethods = paymentMethods,
                    selectedIdentifier = selectedIdentifier
                )
            }.collect {
                _paymentItemModels.value = it
            }
        }
    }

    fun onFetchPaymentMethods(isSwipeRefresh: Boolean = false) {
        fetchPaymentMethodsJob?.cancelIfActive()
        fetchPaymentMethodsJob = viewModelScope.launch {
            getPaymentMethodsUseCase(Unit).collect {
                when (it) {
                    is Resource.Success -> {
                        _paymentMethods.value = it.data
                        _paymentUiState.value = PaymentUiState.Success

                        if (isSwipeRefresh) {
                            _finishSwipeRefresh.offer(Unit)
                        }
                    }
                    is Resource.Error -> {
                        _paymentUiState.value = PaymentUiState.Error(it.message)

                        if (isSwipeRefresh) {
                            _finishSwipeRefresh.offer(Unit)
                        }
                    }
                    is Resource.Loading -> {
                        if (!isSwipeRefresh) {
                            _paymentUiState.value = PaymentUiState.Loading
                        }
                    }
                }
            }
        }
    }

    fun onSelectPaymentMethod(identifier: String) {
        _paymentUiState.value = PaymentUiState.Ready(identifier)
    }

    private fun buildPaymentMethodItemModelsList(
        paymentMethods: List<PaymentMethod>,
        selectedIdentifier: String?
    ): List<PaymentMethodItemModel> {
        return paymentMethods.map {
            paymentMethodUtil.getPaymentMethodItem(it)
        }.map {
            PaymentMethodItemModel(
                paymentMethodItem = it,
                isSelected = selectedIdentifier == it.identifier
            )
        }
    }
}

sealed class PaymentUiState {
    object Success: PaymentUiState()
    data class Ready(val identifier: String) : PaymentUiState()
    data class Error(val message: String?) : PaymentUiState()
    object Loading : PaymentUiState()
}