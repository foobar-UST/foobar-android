package com.foobarust.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.foobarust.data.constants.Constants.SELLERS_COLLECTION
import com.foobarust.data.constants.Constants.SELLER_RATINGS_BASIC_SUB_COLLECTION
import com.foobarust.data.constants.Constants.SELLER_RATING_CREATED_AT_FIELD
import com.foobarust.data.models.seller.SellerRatingBasicDto
import com.foobarust.data.utils.isNetworkData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 3/3/21
 */

class SellerRatingBasicsPagingSource(
    private val firestore: FirebaseFirestore,
    private val sellerId: String
) : PagingSource<Query, SellerRatingBasicDto>() {

    private var initialPageQuery: Query? = null

    override fun getRefreshKey(state: PagingState<Query, SellerRatingBasicDto>): Query? = initialPageQuery

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, SellerRatingBasicDto> {
        return try {
            initialPageQuery = initialPageQuery ?: firestore.collection(
                "${SELLERS_COLLECTION}/$sellerId/$SELLER_RATINGS_BASIC_SUB_COLLECTION"
            )
                .orderBy(SELLER_RATING_CREATED_AT_FIELD, Query.Direction.DESCENDING)
                .limit(params.loadSize.toLong())

            val currentPageQuery = params.key ?: initialPageQuery!!
            val currentPageData = currentPageQuery.get().await()
            val nextPageQuery = if (!currentPageData.isEmpty) {
                val lastVisibleItem = currentPageData.documents[currentPageData.size() - 1]
                initialPageQuery!!.startAfter(lastVisibleItem)
            } else {
                null
            }

            if (!currentPageData.isNetworkData()) {
                throw Exception("Network error.")
            }

            LoadResult.Page(
                data = currentPageData.toObjects(SellerRatingBasicDto::class.java),
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}