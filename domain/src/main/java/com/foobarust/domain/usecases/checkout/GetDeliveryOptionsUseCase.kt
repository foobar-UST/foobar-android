package com.foobarust.domain.usecases.checkout

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.checkout.DeliveryOption
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.repositories.CheckoutRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 1/10/21
 */

class GetDeliveryOptionsUseCase @Inject constructor(
    private val checkoutRepository: CheckoutRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<SellerType, List<DeliveryOption>>(coroutineDispatcher) {

    override fun execute(parameters: SellerType): Flow<Resource<List<DeliveryOption>>> = flow {
        val deliveryOptions = checkoutRepository.getDeliveryOptions(parameters)
        emit(Resource.Success(deliveryOptions))
    }
}