package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerSectionBasic
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 1/19/21
 */

class GetSellerSectionUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GetSellerSectionBasicParameters, SellerSectionBasic>(coroutineDispatcher) {

    override fun execute(
        parameters: GetSellerSectionBasicParameters
    ): Flow<Resource<SellerSectionBasic>> = flow {
        val sectionBasic = sellerRepository.getSellerSection(
            sellerId = parameters.sellerId,
            sectionId = parameters.sectionId
        )
        emit(Resource.Success(sectionBasic))
    }
}

data class GetSellerSectionBasicParameters(
    val sellerId: String,
    val sectionId: String
)