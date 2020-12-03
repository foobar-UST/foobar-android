package com.foobarust.data.pagesource

import androidx.paging.PagingSource
import com.foobarust.data.common.Constants.SELLERS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.SELLER_TYPE_FIELD
import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.models.seller.SellerBasicEntity
import com.foobarust.domain.models.seller.SellerBasic
import com.foobarust.domain.models.seller.SellerType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 9/27/20
 */

class SellerBasicPagingSource(
    private val firestore: FirebaseFirestore,
    private val sellerType: SellerType,
    private val sellerMapper: SellerMapper
) : PagingSource<Query, SellerBasic>() {

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, SellerBasic> {
        return try {
            val requestQuery = firestore.collection(SELLERS_BASIC_COLLECTION)
                .whereEqualTo(SELLER_TYPE_FIELD, sellerType.ordinal)    // 0 for on-campus, 1 for off-campus
                .limit(params.loadSize.toLong())

            val currentPageQuery = params.key ?: requestQuery
            val currentPageData = currentPageQuery.get().await()
            val lastVisible = currentPageData.documents[currentPageData.size() - 1]
            val nextPageQuery = requestQuery.startAfter(lastVisible)

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