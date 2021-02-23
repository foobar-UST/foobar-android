package com.foobarust.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.foobarust.data.constants.Constants.ORDERS_BASIC_COLLECTION
import com.foobarust.data.constants.Constants.ORDER_CRATED_AT_FIELD
import com.foobarust.data.constants.Constants.ORDER_STATE_ARCHIVED
import com.foobarust.data.constants.Constants.ORDER_STATE_DELIVERED
import com.foobarust.data.constants.Constants.ORDER_STATE_FIELD
import com.foobarust.data.models.order.OrderBasicDto
import com.foobarust.data.utils.isNetworkData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 1/30/21
 */

class HistoryOrderBasicsPagingSource(
    private val firestore: FirebaseFirestore
) : PagingSource<Query, OrderBasicDto>() {

    private var initialPageQuery: Query? = null

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, OrderBasicDto> {
        return try {
            initialPageQuery = initialPageQuery ?: firestore.collection(ORDERS_BASIC_COLLECTION)
                .whereIn(
                    ORDER_STATE_FIELD,
                    listOf(ORDER_STATE_ARCHIVED, ORDER_STATE_DELIVERED)
                )
                .orderBy(ORDER_STATE_FIELD, Query.Direction.DESCENDING)
                .orderBy(ORDER_CRATED_AT_FIELD, Query.Direction.DESCENDING)
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
                data = currentPageData.toObjects(OrderBasicDto::class.java),
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Query, OrderBasicDto>): Query? = initialPageQuery
}