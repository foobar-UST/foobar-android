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
) : FlowUseCase<String, SellerSectionDetail>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<SellerSectionDetail>> = flow {
        val sectionDetail = sellerRepository.getSellerSectionDetail(
            sectionId = parameters
        )

        emit(Resource.Success(sectionDetail))
    }
}