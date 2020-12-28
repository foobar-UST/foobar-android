package com.foobarust.domain.usecases.cart

import com.foobarust.domain.common.UseCaseExceptions.ERROR_GET_CART_UPDATED_AT
import com.foobarust.domain.di.MainDispatcher
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// TODO: Get timeout remotely
private const val TIMEOUT_MINUTES = 30

class CheckCartTimeOutUseCase @Inject constructor(
    @MainDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<UserCart, Boolean>(coroutineDispatcher) {

    override suspend fun execute(parameters: UserCart): Boolean {
        val cartTime = parameters.updatedAt?.time ?: throw Exception(ERROR_GET_CART_UPDATED_AT)
        val currentTime = Date().time
        val timeDiffMinutes = TimeUnit.MILLISECONDS.toMinutes(currentTime - cartTime)

        val itemsNotEmpty = parameters.itemsCount > 0

        return timeDiffMinutes >= TIMEOUT_MINUTES && itemsNotEmpty
    }
}