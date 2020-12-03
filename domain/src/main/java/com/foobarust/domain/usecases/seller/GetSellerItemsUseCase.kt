package com.foobarust.domain.usecases.seller

import androidx.paging.PagingData
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerItemBasic
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.usecases.PagingUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by kevin on 10/5/20
 */

class GetSellerItemsUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : PagingUseCase<GetSellerItemsBasicsParameters, SellerItemBasic>(coroutineDispatcher) {

    override fun execute(parameters: GetSellerItemsBasicsParameters): Flow<PagingData<SellerItemBasic>> {
        return sellerRepository.getSellerItems(
            sellerId = parameters.sellerId,
            catalogId = parameters.catalogId
        )
    }
}

data class GetSellerItemsBasicsParameters(
    val sellerId: String,
    val catalogId: String
)