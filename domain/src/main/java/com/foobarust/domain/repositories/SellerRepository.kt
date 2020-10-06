package com.foobarust.domain.repositories

import androidx.paging.PagingData
import com.foobarust.domain.models.SellerBasic
import com.foobarust.domain.models.SellerDetail
import com.foobarust.domain.models.SellerItemBasic
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/27/20
 */

interface SellerRepository {

    fun getSellerList(): Flow<PagingData<SellerBasic>>

    fun getSellerDetail(sellerId: String): Flow<Resource<SellerDetail>>

    fun getSellerItems(catalogId: String): Flow<PagingData<SellerItemBasic>>
}