package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 12/1/20
 */
class GetSellerBasicUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, SellerBasic>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<SellerBasic>> = flow {
        val sellerBasic = sellerRepository.getSellerBasic(sellerId = parameters)
        emit(Resource.Success(sellerBasic))
    }
}