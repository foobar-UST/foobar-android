package com.foobarust.domain.usecases.seller

import androidx.paging.PagingData
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.SellerBasic
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.usecases.PagingUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by kevin on 9/28/20
 */

class GetSellerListUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : PagingUseCase<Unit, SellerBasic>(coroutineDispatcher) {

    override fun execute(parameters: Unit): Flow<PagingData<SellerBasic>> {
        return sellerRepository.getSellerPagedBasics()
    }
}