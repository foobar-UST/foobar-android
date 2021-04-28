package com.foobarust.domain.usecases.cart

import com.foobarust.domain.common.UseCaseExceptions.ERROR_GET_CART_UPDATED_AT
import com.foobarust.domain.di.MainDispatcher
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*
import javax.inject.Inject

class CheckCartTimeoutUseCase @Inject constructor(
    @MainDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<CheckCartTimeoutParameters, Boolean>(coroutineDispatcher) {

    override suspend fun execute(parameters: CheckCartTimeoutParameters): Boolean {
        val cartTime = parameters.userCart.updatedAt?.time ?:
            throw Exception(ERROR_GET_CART_UPDATED_AT)

        val currentTime = Date().time
        val timeDiff = currentTime - cartTime

        val cartNotEmpty = parameters.userCart.itemsCount > 0

        return timeDiff >= parameters.timeoutMills && cartNotEmpty
    }
}

data class CheckCartTimeoutParameters(
    val userCart: UserCart,
    val timeoutMills: Long
)