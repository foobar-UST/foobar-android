package com.foobarust.domain.repositories

import androidx.paging.PagingData
import com.foobarust.domain.models.seller.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/27/20
 */

interface SellerRepository {

    /* Sellers */
    suspend fun getSellerDetail(sellerId: String): SellerDetail

    suspend fun getSellerCatalogs(sellerId: String): List<SellerCatalog>

    fun getSellerBasicsPagingData(sellerType: SellerType): Flow<PagingData<SellerBasic>>

    suspend fun searchSellers(searchQuery: String, numOfSellers: Int): List<SellerBasic>

    /* Items */
    suspend fun getSellerItemDetail(itemId: String): SellerItemDetail

    fun getSellerItemsPagingData(sellerId: String, catalogId: String): Flow<PagingData<SellerItemBasic>>

    suspend fun getRecentSellerItems(sellerId: String, limit: Int): List<SellerItemBasic>

    /* Sections */
    suspend fun getSellerSectionDetail(sectionId: String): SellerSectionDetail

    suspend fun getSellerSectionBasics(sellerId: String, numOfSections: Int): List<SellerSectionBasic>

    fun getAllSellerSectionBasicsPagingData(): Flow<PagingData<SellerSectionBasic>>

    fun getSellerSectionBasicsPagingData(sellerId: String) : Flow<PagingData<SellerSectionBasic>>
}