package com.foobarust.data.paging

import androidx.paging.PagingSource
import com.foobarust.data.common.Constants.SELLERS_COLLECTION
import com.foobarust.data.common.Constants.SELLER_ITEMS_BASIC_SUB_COLLECTION
import com.foobarust.data.common.Constants.SELLER_ITEM_CATALOG_ID_FIELD
import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.models.seller.SellerItemBasicEntity
import com.foobarust.domain.models.seller.SellerItemBasic
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 10/5/20
 */

class SellerItemBasicsPagingSource(
    private val firestore: FirebaseFirestore,
    private val sellerMapper: SellerMapper,
    private val sellerId: String,
    private val catalogId: String
) : PagingSource<Query, SellerItemBasic>() {

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, SellerItemBasic> {
        return try {
            val requestQuery = firestore.collection(
                "$SELLERS_COLLECTION/$sellerId/$SELLER_ITEMS_BASIC_SUB_COLLECTION"
            )
                .whereEqualTo(SELLER_ITEM_CATALOG_ID_FIELD, catalogId)
                //.whereGreaterThan(SELLER_ITEM_COUNT_FIELD, 0)           // Having remaining items
                //.whereEqualTo(SELLER_ITEM_AVAILABLE_FIELD, true)        // Get available items only
                .limit(params.loadSize.toLong())

            val currentPageQuery = params.key ?: requestQuery

            val currentPageData = currentPageQuery.get().await()
            val lastVisible = currentPageData.documents[currentPageData.size() - 1]

            val nextPageQuery = requestQuery.startAfter(lastVisible)

            LoadResult.Page(
                data = currentPageData.toObjects(SellerItemBasicEntity::class.java)
                    .map { sellerMapper.toSellerItemBasic(it) },
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}