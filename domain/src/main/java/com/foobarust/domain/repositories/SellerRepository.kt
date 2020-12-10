package com.foobarust.domain.repositories

import androidx.paging.PagingData
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/27/20
 */

interface SellerRepository {

    fun getSellers(sellerType: SellerType): Flow<PagingData<SellerBasic>>

    suspend fun getSellerBasic(sellerId: String): SellerBasic

    suspend fun getSellerDetail(sellerId: String): SellerDetail

    fun getSellerCatalogsObservable(sellerId: String): Flow<Resource<List<SellerCatalog>>>

    fun getSellerItems(sellerId: String, catalogId: String): Flow<PagingData<SellerItemBasic>>

    suspend fun getSellerItemDetail(sellerId: String, itemId: String): SellerItemDetail
}