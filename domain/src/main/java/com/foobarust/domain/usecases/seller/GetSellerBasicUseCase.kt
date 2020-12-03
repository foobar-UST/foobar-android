package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.usecases.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by kevin on 12/1/20
 */
class GetSellerBasicUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<String, SellerBasic>(coroutineDispatcher) {

    override suspend fun execute(parameters: String): SellerBasic {
        return sellerRepository.getSellerBasic(sellerId = parameters)
    }
}