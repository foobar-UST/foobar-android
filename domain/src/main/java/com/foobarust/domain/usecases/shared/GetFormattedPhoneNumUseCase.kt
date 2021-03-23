package com.foobarust.domain.usecases.shared

import com.foobarust.domain.common.UseCaseExceptions.ERROR_PHONE_NUM_FORMAT_INVALID
import com.foobarust.domain.di.MainDispatcher
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetFormattedPhoneNumUseCase @Inject constructor(
    @MainDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<String, String>(coroutineDispatcher) {

    override suspend fun execute(parameters: String): String {
        if (parameters.length != LENGTH) {
            throw Exception(ERROR_PHONE_NUM_FORMAT_INVALID)
        }

        return "$AREA_CODE $parameters"
    }

    companion object {
        const val LENGTH = 8
        const val AREA_CODE = "+852"
    }
}