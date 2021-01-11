package com.foobarust.android.checkout

import androidx.hilt.lifecycle.ViewModelInject
import com.foobarust.android.common.BaseViewModel
import com.foobarust.domain.usecases.checkout.GetPaymentMethodsUseCase

/**
 * Created by kevin on 1/9/21
 */

class PaymentViewModel @ViewModelInject constructor(
    getPaymentMethodsUseCase: GetPaymentMethodsUseCase
) : BaseViewModel() {


}