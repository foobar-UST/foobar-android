package com.foobarust.data.pagesource

import androidx.paging.PagingSource
import com.foobarust.data.common.Constants.SELLERS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SELLER_TYPE_FIELD
import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.models.SellerBasicEntity
import com.foobarust.domain.models.SellerBasic
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 9/27/20
 */

class SellerOnCampusListPagingSource(
    private val firestore: FirebaseFirestore,
    private val sellerMapper: SellerMapper
) : PagingSource<Query, SellerBasic>() {

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, SellerBasic> {
        return try {
            val currentPageQuery = params.key ?: firestore.collection(SELLERS_BASIC_COLLECTION)
                .whereEqualTo(SELLER_TYPE_FIELD, 0)
                .limit(params.loadSize.toLong())

            val currentPageData = currentPageQuery.get().await()

            val lastVisible = currentPageData.documents[currentPageData.size() - 1]

            val nextPageQuery = firestore.collection(SELLERS_BASIC_COLLECTION)
                .whereEqualTo(SELLER_TYPE_FIELD, 0)
                .startAfter(lastVisible)
                .limit(params.loadSize.toLong())

            LoadResult.Page(
                data = currentPageData.toObjects(SellerBasicEntity::class.java)
                    .map { sellerMapper.toSellerBasic(it) },
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}