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
 * Created by kevin on 1/19/21
 */

class GetMoreSellerItemsUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GetMoreSellerItemsUseCaseParameters, List<SellerItemBasic>>(coroutineDispatcher) {

    override fun execute(parameters: GetMoreSellerItemsUseCaseParameters): Flow<Resource<List<SellerItemBasic>>> = flow {
        val moreItems = sellerRepository.getSellerItemsRecent(
            sellerId = parameters.sellerId,
            limit = parameters.limit
        )
            .filterNot { it.id == parameters.currentItemId }
            .shuffled()

        emit(Resource.Success(moreItems))
    }
}

data class GetMoreSellerItemsUseCaseParameters(
    val sellerId: String,
    val currentItemId: String,
    val limit: Int
)