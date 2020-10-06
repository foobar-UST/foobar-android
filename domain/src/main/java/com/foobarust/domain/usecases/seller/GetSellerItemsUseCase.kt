package com.foobarust.domain.usecases.seller

import androidx.paging.PagingData
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.SellerItemBasic
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
) : PagingUseCase<String, SellerItemBasic>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<PagingData<SellerItemBasic>> {
        // parameters should be catalogId
        return sellerRepository.getSellerItems(catalogId = parameters)
    }
}