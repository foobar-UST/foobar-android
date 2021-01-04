package com.foobarust.domain.usecases.promotion

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.promotion.AdvertiseBasic
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
) : FlowUseCase<Unit, List<AdvertiseBasic>>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<List<AdvertiseBasic>>> = flow {
        emit(Resource.Success(promotionRepository.getAdvertiseBasics()))
    }
}
