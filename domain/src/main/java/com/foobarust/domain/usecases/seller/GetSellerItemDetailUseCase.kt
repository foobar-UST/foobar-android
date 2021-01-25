package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerItemDetail
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 10/13/20
 */

class GetSellerItemDetailUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<GetSellerItemDetailParameters, SellerItemDetail>(coroutineDispatcher) {

    override fun execute(parameters: GetSellerItemDetailParameters): Flow<Resource<SellerItemDetail>> = flow {
        val itemDetail = sellerRepository.getSellerItemDetail(
            sellerId = parameters.sellerId,
            itemId = parameters.itemId
        )
        emit(Resource.Success(itemDetail))
    }
}

data class GetSellerItemDetailParameters(
    val sellerId: String,
    val itemId: String
)