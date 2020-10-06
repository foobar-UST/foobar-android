package com.foobarust.data.paging

import android.util.Log
import androidx.paging.PagingSource
import com.foobarust.data.common.Constants.SELLER_ITEMS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SELLER_ITEMS_CATALOG_ID_FIELD
import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.models.ItemBasicEntity
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
            val lastVisible = currentPageData.documents.last()

            // TODO: show log for demo
            Log.d("SellerItemsPagingSource", "items loaded: ${currentPageData.size()}")

            val nextPageQuery = firestore.collection(SELLER_ITEMS_BASIC_COLLECTION)
                .whereEqualTo(SELLER_ITEMS_CATALOG_ID_FIELD, catalogId)
                .startAfter(lastVisible)
                .limit(params.loadSize.toLong())

            LoadResult.Page(
                data = currentPageData.toObjects(ItemBasicEntity::class.java)
                    .map { sellerMapper.toSellerItemInfo(it) },
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}