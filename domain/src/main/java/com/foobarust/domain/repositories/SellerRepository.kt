package com.foobarust.domain.repositories

import androidx.paging.PagingData
import com.foobarust.domain.models.seller.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/27/20
 */

interface SellerRepository {

    fun getSellerBasics(sellerType: SellerType): Flow<PagingData<SellerBasic>>

    suspend fun getSellerBasic(sellerId: String): SellerBasic

    suspend fun getSellerDetail(sellerId: String): SellerDetail

    suspend fun getSellerCatalogs(sellerId: String): List<SellerCatalog>

    fun getSellerItems(sellerId: String, catalogId: String): Flow<PagingData<SellerItemBasic>>

    suspend fun getSellerItemsRecent(sellerId: String, limit: Int): List<SellerItemBasic>

    suspend fun getSellerItemDetail(sellerId: String, itemId: String): SellerItemDetail

    suspend fun getSellerSectionBasic(sellerId: String, sectionId: String): SellerSectionBasic

    fun getSellerSectionBasics(): Flow<PagingData<SellerSectionBasic>>

    fun getSellerSectionBasicsFor(sellerId: String) : Flow<PagingData<SellerSectionBasic>>

    suspend fun getSellerSectionBasicsFor(sellerId: String, numOfSections: Int): List<SellerSectionBasic>

    suspend fun getSellerSectionDetail(sellerId: String, sectionId: String): SellerSectionDetail
}