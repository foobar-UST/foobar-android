package com.foobarust.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.foobarust.data.common.Constants.SELLERS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SELLERS_CATALOGS_SUB_COLLECTION
import com.foobarust.data.common.Constants.SELLERS_COLLECTION
import com.foobarust.data.common.Constants.SELLER_CATALOG_AVAILABLE_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_SUB_COLLECTION
import com.foobarust.data.common.Constants.SELLER_ITEM_UPDATED_AT_FIELD
import com.foobarust.data.common.Constants.SELLER_SECTIONS_BASIC_SUB_COLLECTION
import com.foobarust.data.common.Constants.SELLER_SECTIONS_SUB_COLLECTION
import com.foobarust.data.common.Constants.SELLER_SECTION_AVAILABLE_FIELD
import com.foobarust.data.common.Constants.SELLER_SECTION_CUTOFF_TIME_FIELD
import com.foobarust.data.common.Constants.SELLER_SECTION_SELLER_NAME_FIELD
import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.paging.SellerBasicsPagingSource
import com.foobarust.data.paging.SellerItemBasicsPagingSource
import com.foobarust.data.paging.SellerSectionsBasicPagingSource
import com.foobarust.data.utils.getAwaitResult
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.repositories.SellerRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

/**
 * Created by kevin on 9/27/20
 */

private const val SELLER_BASICS_PAGE_SIZE = 7
private const val SELLER_ITEMS_PAGE_SIZE = 10
private const val SELLER_SECTIONS_PAGE_SIZE = 8

class SellerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sellerMapper: SellerMapper
) : SellerRepository {

    override suspend fun getSeller(sellerId: String): SellerBasic {
        return firestore.document("$SELLERS_BASIC_COLLECTION/$sellerId")
            .getAwaitResult(sellerMapper::toSellerBasic)
    }

    override suspend fun getSellerDetail(sellerId: String): SellerDetail {
        return firestore.document("$SELLERS_COLLECTION/$sellerId")
            .getAwaitResult(sellerMapper::toSellerDetail)
    }

    override suspend fun getSellerCatalogs(sellerId: String): List<SellerCatalog> {
        return firestore.collection(
            "$SELLERS_COLLECTION/$sellerId/$SELLERS_CATALOGS_SUB_COLLECTION"
        )
            .whereEqualTo(SELLER_CATALOG_AVAILABLE_FIELD, true)     // Get available catalogs only
            .getAwaitResult(sellerMapper::toSellerCatalog)
    }

    override fun getSellersPagingData(sellerType: SellerType): Flow<PagingData<SellerBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_BASICS_PAGE_SIZE * 2,
                pageSize = SELLER_BASICS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SellerBasicsPagingSource(firestore, sellerType) }
        ).flow.map { pagingData ->
            pagingData.map { sellerMapper.toSellerBasic(it) }
        }
    }

    override suspend fun getSellerItemDetail(sellerId: String, itemId: String): SellerItemDetail {
        return firestore.document(
            "$SELLERS_COLLECTION/$sellerId/$SELLER_ITEMS_SUB_COLLECTION/$itemId"
        )
            .getAwaitResult(sellerMapper::toSellerItemDetail)
    }

    override fun getSellerItemsPagingData(sellerId: String, catalogId: String): Flow<PagingData<SellerItemBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_ITEMS_PAGE_SIZE * 2,
                pageSize = SELLER_ITEMS_PAGE_SIZE
            ),
            pagingSourceFactory = {
                SellerItemBasicsPagingSource(firestore, sellerId, catalogId)
            }
        ).flow.map { pagingData ->
            pagingData.map { sellerMapper.toSellerItemBasic(it) }
        }
    }

    override suspend fun getRecentSellerItems(
        sellerId: String,
        limit: Int
    ): List<SellerItemBasic> {
        return firestore.collection(
            "$SELLERS_COLLECTION/$sellerId/$SELLER_ITEMS_SUB_COLLECTION"
        )
            .orderBy(SELLER_ITEM_UPDATED_AT_FIELD, Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .getAwaitResult(sellerMapper::toSellerItemBasic)
    }

    override suspend fun getSellerSection(sellerId: String, sectionId: String): SellerSectionBasic {
        return firestore.document(
            "$SELLERS_COLLECTION/$sellerId/$SELLER_SECTIONS_BASIC_SUB_COLLECTION/$sectionId"
        )
            .getAwaitResult(sellerMapper::toSellerSectionBasic)
    }

    override suspend fun getSellerSectionDetail(sellerId: String, sectionId: String): SellerSectionDetail {
        return firestore.document(
            "$SELLERS_COLLECTION/$sellerId/$SELLER_SECTIONS_SUB_COLLECTION/$sectionId"
        )
            .getAwaitResult(sellerMapper::toSellerSectionDetail)
    }

    override suspend fun getSellerSections(
        sellerId: String,
        numOfSections: Int
    ): List<SellerSectionBasic> {
        return firestore.collection(
            "$SELLERS_COLLECTION/$sellerId/$SELLER_SECTIONS_BASIC_SUB_COLLECTION"
        )
            .whereEqualTo(SELLER_SECTION_AVAILABLE_FIELD, true)
            .whereGreaterThan(SELLER_SECTION_CUTOFF_TIME_FIELD, Date())
            .orderBy(SELLER_SECTION_CUTOFF_TIME_FIELD, Query.Direction.ASCENDING)
            .orderBy(SELLER_SECTION_SELLER_NAME_FIELD, Query.Direction.ASCENDING)
            .limit(numOfSections.toLong())
            .getAwaitResult(sellerMapper::toSellerSectionBasic)
    }

    override fun getSellerSectionsPagingData(sellerId: String): Flow<PagingData<SellerSectionBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_SECTIONS_PAGE_SIZE * 2,
                pageSize = SELLER_SECTIONS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SellerSectionsBasicPagingSource(firestore, sellerId)
            }
        ).flow.map { pagingData ->
            pagingData.map { sellerMapper.toSellerSectionBasic(it) }
        }
    }

    override fun getAllSellerSectionsPagingData(): Flow<PagingData<SellerSectionBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_SECTIONS_PAGE_SIZE * 2,
                pageSize = SELLER_SECTIONS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SellerSectionsBasicPagingSource(firestore)
            }
        ).flow.map { pagingData ->
            pagingData.map { sellerMapper.toSellerSectionBasic(it) }
        }
    }
}