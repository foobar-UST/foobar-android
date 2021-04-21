package com.foobarust.domain.usecases.seller

import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.explore.ItemCategory
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.foobarust.domain.usecases.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by kevin on 2/28/21
 */

class GetSellerItemCategoryUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, ItemCategory>(coroutineDispatcher) {

    override fun execute(parameters: String): Flow<Resource<ItemCategory>> = flow {
        val itemCategory = sellerRepository.getItemCategory(categoryTag = parameters)
        emit(Resource.Success(itemCategory))
    }
}