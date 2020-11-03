package com.foobarust.domain.repositories

import androidx.paging.PagingData
import com.foobarust.domain.models.SellerBasic
import com.foobarust.domain.models.SellerDetail
import com.foobarust.domain.models.SellerItemBasic
import com.foobarust.domain.models.SellerItemDetail
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/27/20
 */

interface SellerRepository {

    fun getSellerPagedBasics(): Flow<PagingData<SellerBasic>>

    fun getSellerDetailObservable(sellerId: String): Flow<Resource<SellerDetail>>

    fun getSellerPagedItems(catalogId: String): Flow<PagingData<SellerItemBasic>>

    suspend fun getSellerItemDetail(itemId: String): SellerItemDetail
}