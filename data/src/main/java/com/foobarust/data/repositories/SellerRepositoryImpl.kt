package com.foobarust.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.foobarust.data.common.Constants
import com.foobarust.data.common.Constants.SELLERS_COLLECTION
import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.pagesource.SellerItemsPagingSource
import com.foobarust.data.pagesource.SellerOnCampusListPagingSource
import com.foobarust.data.utils.getAwaitResult
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.SellerBasic
import com.foobarust.domain.models.SellerDetail
import com.foobarust.domain.models.SellerItemBasic
import com.foobarust.domain.models.SellerItemDetail
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

    override fun getSellerPagedBasics(): Flow<PagingData<SellerBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_BASIC_LIST_PAGE_SIZE * 2,
                pageSize = SELLER_BASIC_LIST_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SellerOnCampusListPagingSource(firestore, sellerMapper) }
        ).flow
    }

    override fun getSellerDetailObservable(sellerId: String): Flow<Resource<SellerDetail>> {
        return firestore.collection(SELLERS_COLLECTION).document(sellerId)
            .snapshotFlow(sellerMapper::toSellerDetail)
    }

    override fun getSellerPagedItems(catalogId: String): Flow<PagingData<SellerItemBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_ITEMS_LIST_PAGE_SIZE * 2,
                pageSize = SELLER_ITEMS_LIST_PAGE_SIZE
            ),
            pagingSourceFactory = { SellerItemsPagingSource(firestore, sellerMapper, catalogId) }
        ).flow
    }

    override suspend fun getSellerItemDetail(itemId: String): SellerItemDetail {
        return firestore.collection(Constants.SELLER_ITEMS_COLLECTION).document(itemId)
            .getAwaitResult(sellerMapper::toSellerItemDetail)
    }
}