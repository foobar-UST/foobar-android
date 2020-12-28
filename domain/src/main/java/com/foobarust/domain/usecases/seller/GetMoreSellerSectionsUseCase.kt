package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerSectionBasic
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 12/27/20
 */

class GetMoreSellerSectionsUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<GetMoreSellerSectionsParameters, List<SellerSectionBasic>>(coroutineDispatcher) {

    override suspend fun execute(parameters: GetMoreSellerSectionsParameters): List<SellerSectionBasic> {
        return sellerRepository.getSellerSectionBasics(
            sellerId = parameters.sellerId,
            numOfSections = parameters.numOfSections
        )
    }
}

data class GetMoreSellerSectionsParameters(
    val sellerId: String,
    val numOfSections: Int
)