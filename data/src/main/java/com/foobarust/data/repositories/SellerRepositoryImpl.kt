package com.foobarust.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.foobarust.data.common.Constants.SELLERS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SELLERS_CATALOGS_SUB_COLLECTION
import com.foobarust.data.common.Constants.SELLERS_COLLECTION
import com.foobarust.data.common.Constants.SELLER_CATALOG_AVAILABLE_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_SUB_COLLECTION
import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.pagesource.SellerBasicPagingSource
import com.foobarust.data.pagesource.SellerItemsPagingSource
import com.foobarust.data.utils.getAwaitResult
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by kevin on 9/27/20
 */

private const val SELLER_BASIC_LIST_PAGE_SIZE = 7
private const val SELLER_ITEMS_LIST_PAGE_SIZE = 10

class SellerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sellerMapper: SellerMapper
) : SellerRepository {

    override fun getSellers(sellerType: SellerType): Flow<PagingData<SellerBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_BASIC_LIST_PAGE_SIZE * 2,
                pageSize = SELLER_BASIC_LIST_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SellerBasicPagingSource(firestore, sellerType, sellerMapper)
            }
        ).flow
    }

    override suspend fun getSellerBasic(sellerId: String): SellerBasic {
        return firestore.collection(SELLERS_BASIC_COLLECTION)
            .document(sellerId)
            .getAwaitResult(sellerMapper::toSellerBasic)
    }

    override suspend fun getSellerDetail(sellerId: String): SellerDetail {
        return firestore.collection(SELLERS_COLLECTION)
            .document(sellerId)
            .getAwaitResult(sellerMapper::toSellerDetail)
    }

    override fun getSellerCatalogsObservable(sellerId: String): Flow<Resource<List<SellerCatalog>>> {
        return firestore.collection(SELLERS_COLLECTION)
            .document(sellerId)
            .collection(SELLERS_CATALOGS_SUB_COLLECTION)
            .whereEqualTo(SELLER_CATALOG_AVAILABLE_FIELD, true)     // Get available catalogs only
            .snapshotFlow(sellerMapper::toSellerCatalog)
    }

    override fun getSellerItems(sellerId: String, catalogId: String): Flow<PagingData<SellerItemBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_ITEMS_LIST_PAGE_SIZE * 2,
                pageSize = SELLER_ITEMS_LIST_PAGE_SIZE
            ),
            pagingSourceFactory = {
                SellerItemsPagingSource(firestore, sellerMapper, sellerId, catalogId)
            }
        ).flow
    }

    override suspend fun getSellerItemDetail(sellerId: String, itemId: String): SellerItemDetail {
        return firestore.collection(SELLERS_COLLECTION)
            .document(sellerId)
            .collection(SELLER_ITEMS_SUB_COLLECTION)
            .document(itemId)
            .getAwaitResult(sellerMapper::toSellerItemDetail)
    }
}