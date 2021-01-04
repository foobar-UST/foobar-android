package com.foobarust.domain.usecases.seller

import androidx.paging.PagingData
import androidx.paging.filter
import com.foobarust.domain.di.IoDispatcher
import com.foobarust.domain.models.seller.SellerSectionBasic
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.usecases.PagingUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by kevin on 12/20/20
 */

class GetSellerSectionsUseCase @Inject constructor(
    private val sellerRepository: SellerRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : PagingUseCase<GetSellerSectionsParameters, SellerSectionBasic>(coroutineDispatcher) {

    override fun execute(parameters: GetSellerSectionsParameters): Flow<PagingData<SellerSectionBasic>> {
        return if (parameters.isGetAllSections()) {
            sellerRepository.getSellerSectionBasics()
        } else {
            sellerRepository.getSellerSectionsBasic(sellerId = parameters.sellerId!!)
                .map { pagingData ->
                    // Filter out current section
                    pagingData.filter { it.id != parameters.currentSectionId }
                }
        }
    }
}

data class GetSellerSectionsParameters(
    val sellerId: String? = null,
    val currentSectionId: String? = null
) {
    fun isGetAllSections(): Boolean = sellerId == null
}