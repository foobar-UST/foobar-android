package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerSectionDetail
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 12/27/20
 */

class GetSellerSectionDetailUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GetSellerSectionDetailParameters, SellerSectionDetail>(coroutineDispatcher) {

    override fun execute(
        parameters: GetSellerSectionDetailParameters
    ): Flow<Resource<SellerSectionDetail>> = flow {
        val sectionDetail = sellerRepository.getSellerSectionDetail(
            sellerId = parameters.sellerId,
            sectionId = parameters.sectionId
        )

        emit(Resource.Success(sectionDetail))
    }
}

data class GetSellerSectionDetailParameters(
    val sellerId: String,
    val sectionId: String
)