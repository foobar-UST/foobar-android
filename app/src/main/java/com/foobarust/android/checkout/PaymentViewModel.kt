package com.foobarust.android.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.foobarust.android.common.BaseViewModel
import com.foobarust.android.common.UiState
import com.foobarust.domain.models.checkout.PaymentMethod
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.checkout.GetPaymentMethodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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

    private val _paymentMethodItemModels = MutableStateFlow<List<PaymentMethodItemModel>>(emptyList())
    val paymentMethodItemModels: LiveData<List<PaymentMethodItemModel>> = _paymentMethodItemModels
        .asLiveData(viewModelScope.coroutineContext)

    private val _selectedPaymentMethod = MutableStateFlow<String?>(null)

    // Allow payment if the user has selected one of the payment methods
    val allowProceedPayment: LiveData<Boolean> = _selectedPaymentMethod
        .map { it != null }
        .asLiveData(viewModelScope.coroutineContext)

    init {
        fetchPaymentMethods()
    }

    fun onRestoreSelectPaymentMethod(identifier: String?) {
        _selectedPaymentMethod.value = identifier
    }

    private fun fetchPaymentMethods() = viewModelScope.launch {
        getPaymentMethodsUseCase(Unit).combine(_selectedPaymentMethod) { paymentMethods, selectedMethod ->
            when (paymentMethods) {
                is Resource.Success -> {
                    setUiState(UiState.Success)
                    buildPaymentMethodItemModelsList(
                        paymentMethods.data,
                        selectedMethod
                    )
                }
                is Resource.Error -> {
                    setUiState(UiState.Error(paymentMethods.message))
                    emptyList()
                }
                is Resource.Loading -> {
                    setUiState(UiState.Loading)
                    emptyList()
                }
            }
        }.collect { _paymentMethodItemModels.value = it }
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