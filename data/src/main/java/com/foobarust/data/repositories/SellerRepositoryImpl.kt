package com.foobarust.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.foobarust.data.common.Constants.SELLERS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SELLERS_CATALOGS_SUB_COLLECTION
import com.foobarust.data.common.Constants.SELLERS_COLLECTION
import com.foobarust.data.common.Constants.SELLER_CATALOG_AVAILABLE_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_SUB_COLLECTION
import com.foobarust.data.common.Constants.SELLER_SECTIONS_BASIC_SUB_COLLECTION
import com.foobarust.data.common.Constants.SELLER_SECTIONS_SUB_COLLECTION
import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.paging.SellerBasicsPagingSource
import com.foobarust.data.paging.SellerItemBasicsPagingSource
import com.foobarust.data.paging.SellerSectionsBasicPagingSource
import com.foobarust.data.utils.getAwaitResult
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.repositories.SellerRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by kevin on 9/27/20
 */

private const val SELLER_BASICS_LIST_PAGE_SIZE = 7
private const val SELLER_ITEMS_LIST_PAGE_SIZE = 10
private const val SELLER_SECTIONS_LIST_PAGE_SIZE = 8

class SellerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sellerMapper: SellerMapper
) : SellerRepository {

    override fun getSellerBasics(sellerType: SellerType): Flow<PagingData<SellerBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_BASICS_LIST_PAGE_SIZE * 2,
                pageSize = SELLER_BASICS_LIST_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SellerBasicsPagingSource(firestore, sellerType, sellerMapper)
            }
        ).flow
    }

    override suspend fun getSellerBasic(sellerId: String): SellerBasic {
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

    override fun getSellerItems(sellerId: String, catalogId: String): Flow<PagingData<SellerItemBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_ITEMS_LIST_PAGE_SIZE * 2,
                pageSize = SELLER_ITEMS_LIST_PAGE_SIZE
            ),
            pagingSourceFactory = {
                SellerItemBasicsPagingSource(firestore, sellerMapper, sellerId, catalogId)
            }
        ).flow
    }

    override suspend fun getSellerItemDetail(sellerId: String, itemId: String): SellerItemDetail {
        return firestore.document(
            "$SELLERS_COLLECTION/$sellerId/$SELLER_ITEMS_SUB_COLLECTION/$itemId"
        )
            .getAwaitResult(sellerMapper::toSellerItemDetail)
    }

    override fun getSellerSectionBasics(): Flow<PagingData<SellerSectionBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_SECTIONS_LIST_PAGE_SIZE * 2,
                pageSize = SELLER_SECTIONS_LIST_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SellerSectionsBasicPagingSource(firestore, sellerMapper)
            }
        ).flow
    }

    override suspend fun getSellerSectionBasics(
        sellerId: String,
        numOfSections: Int
    ): List<SellerSectionBasic> {
        return firestore.collection(
            "$SELLERS_COLLECTION/$sellerId/$SELLER_SECTIONS_BASIC_SUB_COLLECTION"
        )
            .limit(numOfSections.toLong())
            .getAwaitResult(sellerMapper::toSellerSectionBasic)
    }

    override suspend fun getSellerSectionDetail(sellerId: String, sectionId: String): SellerSectionDetail {
        return firestore.document(
            "$SELLERS_COLLECTION/$sellerId/$SELLER_SECTIONS_SUB_COLLECTION/$sectionId"
        )
            .getAwaitResult(sellerMapper::toSellerSectionDetail)
    }
}