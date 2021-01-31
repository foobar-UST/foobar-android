package com.foobarust.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.foobarust.data.common.Constants.ORDERS_BASIC_COLLECTION
import com.foobarust.data.common.Constants.ORDER_STATE_ARCHIVED
import com.foobarust.data.common.Constants.ORDER_STATE_FIELD
import com.foobarust.data.mappers.OrderMapper
import com.foobarust.data.models.order.OrderBasicDto
import com.foobarust.data.utils.isNetworkData
import com.foobarust.domain.models.order.OrderBasic
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 1/30/21
 */

class ArchivedOrderBasicsPagingSource(
    private val firestore: FirebaseFirestore,
    private val orderMapper: OrderMapper
) : PagingSource<Query, OrderBasic>() {

    private var initialPageQuery: Query? = null

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, OrderBasic> {
        return try {
            initialPageQuery = initialPageQuery ?: firestore.collection(ORDERS_BASIC_COLLECTION)
                .whereEqualTo(ORDER_STATE_FIELD, ORDER_STATE_ARCHIVED)
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
                data = currentPageData.toObjects(OrderBasicDto::class.java)
                    .map { orderMapper.toOrderBasic(it) },
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Query, OrderBasic>): Query? = initialPageQuery
}