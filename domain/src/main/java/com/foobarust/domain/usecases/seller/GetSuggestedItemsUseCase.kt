package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerItemBasic
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 2/20/21
 */

class GetSuggestedItemsUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GetSuggestedItemsParameters, List<SellerItemBasic>>(coroutineDispatcher) {

    override fun execute(
        parameters: GetSuggestedItemsParameters
    ): Flow<Resource<List<SellerItemBasic>>> = flow {
        val suggestedItems = sellerRepository.getRecentSellerItems(
            sellerId = parameters.sellerId,
            limit = parameters.numOfItems
        )
            .filterNot { it.id == parameters.ignoreItemId }
            .shuffled()

        emit(Resource.Success(suggestedItems))
    }
}

data class GetSuggestedItemsParameters(
    val sellerId: String,
    val ignoreItemId: String,
    val numOfItems: Int
)