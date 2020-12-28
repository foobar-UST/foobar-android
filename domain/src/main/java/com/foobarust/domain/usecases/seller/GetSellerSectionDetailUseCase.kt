package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerSectionDetail
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 12/27/20
 */

class GetSellerSectionDetailUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<GetSellerSectionDetailParameters, SellerSectionDetail>(coroutineDispatcher) {

    override suspend fun execute(parameters: GetSellerSectionDetailParameters): SellerSectionDetail {
        return sellerRepository.getSellerSectionDetail(
            sellerId = parameters.sellerId,
            sectionId = parameters.sectionId
        )
    }
}

data class GetSellerSectionDetailParameters(
    val sellerId: String,
    val sectionId: String
)