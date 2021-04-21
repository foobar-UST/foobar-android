package com.foobarust.domain.repositories

import androidx.paging.PagingData
import com.foobarust.domain.models.explore.ItemCategory
import com.foobarust.domain.models.seller.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/27/20
 */

interface SellerRepository {

    suspend fun getSellerDetail(sellerId: String): SellerDetail

    suspend fun getSellerCatalogs(sellerId: String): List<SellerCatalog>

    fun getSellerBasicsPagingData(sellerBasicsFilter: SellerBasicsFilter): Flow<PagingData<SellerBasic>>

    suspend fun searchSellers(searchQuery: String, numOfSellers: Int): List<SellerBasic>

    suspend fun getSellerItemDetail(itemId: String): SellerItemDetail

    fun getSellerItemsPagingData(sellerId: String, catalogId: String): Flow<PagingData<SellerItemBasic>>

    suspend fun getRecentSellerItems(sellerId: String, limit: Int): List<SellerItemBasic>

    suspend fun getSellerSectionDetail(sectionId: String): SellerSectionDetail

    suspend fun getSellerSectionBasics(sellerId: String, numOfSections: Int): List<SellerSectionBasic>

    fun getAllSellerSectionBasicsPagingData(): Flow<PagingData<SellerSectionBasic>>

    fun getSellerSectionBasicsPagingData(sellerId: String) : Flow<PagingData<SellerSectionBasic>>

    suspend fun getItemCategories(): List<ItemCategory>

    suspend fun getItemCategory(categoryTag: String): ItemCategory

    fun getSellerRatingsPagingData(
        sellerId: String,
        sortOption: SellerRatingSortOption
    ): Flow<PagingData<SellerRatingBasic>>
}