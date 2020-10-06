package com.foobarust.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.foobarust.data.common.Constants.SELLERS_COLLECTION
import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.paging.SellerItemsPagingSource
import com.foobarust.data.paging.SellerListPagingSource
import com.foobarust.data.utils.snapshotFlow
import com.foobarust.domain.models.SellerBasic
import com.foobarust.domain.models.SellerDetail
import com.foobarust.domain.models.SellerItemBasic
import com.foobarust.domain.repositories.SellerRepository
import com.foobarust.domain.states.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by kevin on 9/27/20
 */

class SellerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sellerMapper: SellerMapper
) : SellerRepository {

    override fun getSellerList(): Flow<PagingData<SellerBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_LIST_ITEMS_PAGE_SIZE * 2,
                pageSize = SELLER_LIST_ITEMS_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SellerListPagingSource(firestore, sellerMapper) }
        ).flow
    }

    override fun getSellerDetail(sellerId: String): Flow<Resource<SellerDetail>> {
        return firestore.collection(SELLERS_COLLECTION).document(sellerId)
            .snapshotFlow(sellerMapper::toSellerDetail)
    }

    override fun getSellerItems(catalogId: String): Flow<PagingData<SellerItemBasic>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = SELLER_ITEMS_PAGE_SIZE * 2,
                pageSize = SELLER_ITEMS_PAGE_SIZE
            ),
            pagingSourceFactory = { SellerItemsPagingSource(firestore, sellerMapper, catalogId) }
        ).flow
    }

    companion object {
        private const val SELLER_LIST_ITEMS_PAGE_SIZE = 7
        private const val SELLER_ITEMS_PAGE_SIZE = 10
    }
}