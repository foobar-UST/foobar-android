package com.foobarust.data.pagesource

import androidx.paging.PagingSource
import com.foobarust.data.common.Constants.SELLER_ITEMS_AVAILABLE_FIELD
import com.foobarust.data.common.Constants.SELLER_ITEMS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SELLER_ITEMS_CATALOG_ID_FIELD
import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.models.SellerItemBasicEntity
import com.foobarust.domain.models.SellerItemBasic
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 10/5/20
 */

class SellerItemsPagingSource(
    private val firestore: FirebaseFirestore,
    private val sellerMapper: SellerMapper,
    private val catalogId: String
) : PagingSource<Query, SellerItemBasic>() {

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, SellerItemBasic> {
        return try {
            val currentPageQuery = params.key ?:
                firestore.collection(SELLER_ITEMS_BASIC_COLLECTION)
                    .whereEqualTo(SELLER_ITEMS_CATALOG_ID_FIELD, catalogId)
                    .limit(params.loadSize.toLong())

            val currentPageData = currentPageQuery.get().await()
            val lastVisible = currentPageData.documents[currentPageData.size() - 1]

            val nextPageQuery = firestore.collection(SELLER_ITEMS_BASIC_COLLECTION)
                .whereEqualTo(SELLER_ITEMS_CATALOG_ID_FIELD, catalogId)
                .whereEqualTo(SELLER_ITEMS_AVAILABLE_FIELD, true)
                .startAfter(lastVisible)
                .limit(params.loadSize.toLong())

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