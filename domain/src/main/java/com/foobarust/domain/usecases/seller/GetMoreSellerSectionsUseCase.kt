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
 * Created by kevin on 12/27/20
 */

class GetMoreSellerSectionsUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GetMoreSellerSectionsParameters, List<SellerSectionBasic>>(coroutineDispatcher) {

    override fun execute(
        parameters: GetMoreSellerSectionsParameters
    ): Flow<Resource<List<SellerSectionBasic>>> = flow {
        val sections = sellerRepository.getSellerSectionBasicsFor(
            sellerId = parameters.sellerId,
            numOfSections = parameters.numOfSections
        )
            .filter { it.id != parameters.currentSectionId  }

        emit(Resource.Success(sections))
    }
}

data class GetMoreSellerSectionsParameters(
    val sellerId: String,
    val numOfSections: Int,
    val currentSectionId: String
)