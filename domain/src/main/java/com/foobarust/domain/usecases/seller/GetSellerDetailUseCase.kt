package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.SellerDetail
import com.foobarust.domain.models.removeNonPurchasableCatalogs
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

/**
 * Created by kevin on 10/4/20
 */

class GetSellerDetailUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, SellerDetail>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<SellerDetail>> {
        // Parameters is sellerId
        return sellerRepository.getSellerDetailObservable(parameters).mapLatest { resource ->
            when (resource) {
                is Resource.Success -> {
                    Resource.Success(
                        resource.data.removeNonPurchasableCatalogs()
                    )
                }
                is Resource.Error -> resource
                is Resource.Loading -> resource
            }
        }
    }
}