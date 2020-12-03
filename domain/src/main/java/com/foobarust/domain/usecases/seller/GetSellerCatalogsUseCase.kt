package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerCatalog
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by kevin on 11/30/20
 */
class GetSellerCatalogsUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, List<SellerCatalog>>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<List<SellerCatalog>>> {
        return sellerRepository.getSellerCatalogsObservable(parameters)
    }
}