package com.foobarust.domain.usecases.promotion

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.repositories.PromotionRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 9/28/20
 */

class GetAdvertiseBasicsUseCase @Inject constructor(
    private val promotionRepository: PromotionRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GetAdvertiseBasicsParameters, List<AdvertiseBasic>>(coroutineDispatcher) {

    override fun execute(parameters: GetAdvertiseBasicsParameters): Flow<Resource<List<AdvertiseBasic>>> = flow {
        val advertiseBasics = promotionRepository.getAdvertiseBasics(
            sellerType = parameters.sellerType,
            numOfAdvertises = parameters.numOfAdvertises
        )
        emit(Resource.Success(advertiseBasics))
    }
}

data class GetAdvertiseBasicsParameters(
    val sellerType: SellerType,
    val numOfAdvertises: Int
)
