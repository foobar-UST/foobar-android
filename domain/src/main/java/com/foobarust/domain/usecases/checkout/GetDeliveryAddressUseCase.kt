package com.foobarust.domain.usecases.checkout

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.models.seller.getNormalizedAddress
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 1/11/21
 */

class GetDeliveryAddressUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<UserCart, String>(coroutineDispatcher) {

    override fun execute(parameters: UserCart): Flow<Resource<String>> = flow {
        val sellerType = parameters.sellerType ?: throw Exception("Error getting seller type.")

        val deliveryAddress =  if (sellerType == SellerType.ON_CAMPUS) {
            val sellerDetail = sellerRepository.getSellerDetail(sellerId = parameters.sellerId!!)
            sellerDetail.getNormalizedAddress()
        } else {
            /*
            val sectionDetail = sellerRepository.getSellerSectionDetail(
                sellerId = parameters.sellerId!!,
                sectionId = parameters.sectionId!!
            )
            sectionDetail.getNormalizedDeliveryLocation()

             */
            "" // Todo: fix delivery address
        }

        emit(Resource.Success(deliveryAddress))
    }
}