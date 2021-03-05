package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.explore.SellerItemCategory
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 2/27/21
 */

class GetSellerItemCategoriesUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<SellerItemCategory>>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<Resource<List<SellerItemCategory>>> = flow {
        val itemCategories = sellerRepository.getSellerItemCategories()
        emit(Resource.Success(itemCategories))
    }
}