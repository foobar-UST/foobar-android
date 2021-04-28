package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerDetailWithCatalogs
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSellerDetailWithCatalogsUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, SellerDetailWithCatalogs>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<SellerDetailWithCatalogs>> = flow {
        val sellerDetail = sellerRepository.getSellerDetail(sellerId = parameters)
        val sellerCatalogs = sellerRepository.getSellerCatalogs(sellerId = parameters)

        val sellerDetailWithCatalogs = SellerDetailWithCatalogs(
            sellerDetail = sellerDetail,
            sellerCatalogs = sellerCatalogs
        )

        emit(Resource.Success(sellerDetailWithCatalogs))
    }
}