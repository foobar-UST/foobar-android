package com.foobarust.domain.usecases.seller

import androidx.paging.PagingData
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerRatingBasic
import com.foobarust.domain.models.seller.SellerRatingSortOption
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.usecases.PagingUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by kevin on 3/4/21
 */

class GetSellerRatingsPagingUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : PagingUseCase<GetSellerRatingsPagingParameter, SellerRatingBasic>(coroutineDispatcher) {

    override fun execute(
        parameters: GetSellerRatingsPagingParameter
    ): Flow<PagingData<SellerRatingBasic>> {
        return sellerRepository.getSellerRatingsPagingData(
            sellerId = parameters.sellerId,
            sortOption = parameters.sortOption
        )
    }
}

data class GetSellerRatingsPagingParameter(
    val sellerId: String,
    val sortOption: SellerRatingSortOption
)