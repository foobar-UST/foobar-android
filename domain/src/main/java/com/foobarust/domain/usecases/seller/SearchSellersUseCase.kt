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
 * Created by kevin on 2/23/21
 */

class SearchSellersUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<SearchSellersParameters, List<SellerBasic>>(coroutineDispatcher) {

    override fun execute(parameters: SearchSellersParameters): Flow<Resource<List<SellerBasic>>> = flow {
        val searchQuery = parameters.searchQuery

        if (searchQuery.isBlank()) {
            emit(Resource.Success(emptyList()))
            return@flow
        } else {
            val sellerBasics = sellerRepository.searchSellers(
                searchQuery = searchQuery,
                numOfSellers = parameters.numOfSellers
            )

            emit(Resource.Success(sellerBasics))
        }
    }
}

data class SearchSellersParameters(
    val searchQuery: String,
    val numOfSellers: Int
)