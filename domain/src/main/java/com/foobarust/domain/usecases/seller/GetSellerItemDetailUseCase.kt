package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.SellerItemDetail
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 10/13/20
 */

class GetSellerItemDetailUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<String, SellerItemDetail>(coroutineDispatcher) {

    override suspend fun execute(parameters: String): SellerItemDetail {
        return sellerRepository.getSellerItemDetail(itemId = parameters)
    }
}