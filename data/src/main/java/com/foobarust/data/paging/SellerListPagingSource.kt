package com.foobarust.data.paging

import android.util.Log
import androidx.paging.PagingSource
import com.foobarust.data.common.Constants.SELLERS_BASIC_COLLECTION
import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.models.SellerBasicEntity
import com.foobarust.domain.models.SellerBasic
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 9/27/20
 */

class SellerListPagingSource(
    private val firestore: FirebaseFirestore,
    private val sellerMapper: SellerMapper
) : PagingSource<Query, SellerBasic>() {

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, SellerBasic> {
        return try {
            val currentPageQuery = params.key ?:
                firestore.collection(SELLERS_BASIC_COLLECTION)
                    .limit(params.loadSize.toLong())

            val currentPageData = currentPageQuery.get().await()

            // TODO: show log for demo
            Log.d("SellerListPagingSource", "sellers loaded: ${currentPageData.size()}")

            val lastVisible = currentPageData.documents.last()

            val nextPageQuery = firestore.collection(SELLERS_BASIC_COLLECTION)
                .startAfter(lastVisible)
                .limit(params.loadSize.toLong())

            LoadResult.Page(
                data = currentPageData.toObjects(SellerBasicEntity::class.java)
                    .map { sellerMapper.toSellerListItem(it) },
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}