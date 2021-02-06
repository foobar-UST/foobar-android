package com.foobarust.domain.repositories

import androidx.paging.PagingData
import com.foobarust.domain.models.seller.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by kevin on 9/27/20
 */

interface SellerRepository {

    /*
        Sellers
     */
    suspend fun getSeller(sellerId: String): SellerBasic

    suspend fun getSellerDetail(sellerId: String): SellerDetail

    suspend fun getSellerCatalogs(sellerId: String): List<SellerCatalog>

    fun getSellersPagingData(sellerType: SellerType): Flow<PagingData<SellerBasic>>

    /*
        Seller items
     */
    suspend fun getSellerItemDetail(sellerId: String, itemId: String): SellerItemDetail

    fun getSellerItemsPagingData(sellerId: String, catalogId: String): Flow<PagingData<SellerItemBasic>>

    suspend fun getRecentSellerItems(sellerId: String, limit: Int): List<SellerItemBasic>

    /*
        Seller sections
     */
    suspend fun getSellerSection(sellerId: String, sectionId: String): SellerSectionBasic

    suspend fun getSellerSectionDetail(sellerId: String, sectionId: String): SellerSectionDetail

    suspend fun getSellerSections(sellerId: String, numOfSections: Int): List<SellerSectionBasic>

    fun getAllSellerSectionsPagingData(): Flow<PagingData<SellerSectionBasic>>

    fun getSellerSectionsPagingData(sellerId: String) : Flow<PagingData<SellerSectionBasic>>
}