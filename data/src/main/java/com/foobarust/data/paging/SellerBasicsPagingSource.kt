package com.foobarust.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.foobarust.data.constants.Constants.SELLERS_BASIC_COLLECTION
import com.foobarust.data.constants.Constants.SELLER_TAGS_FIELD
import com.foobarust.data.constants.Constants.SELLER_TYPE_FIELD
import com.foobarust.data.models.seller.SellerBasicDto
import com.foobarust.data.utils.isNetworkData
import com.foobarust.domain.models.seller.SellerBasicsFilter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 9/27/20
 */

class SellerBasicsPagingSource(
    private val firestore: FirebaseFirestore,
    private val sellerBasicsFilter: SellerBasicsFilter
) : PagingSource<Query, SellerBasicDto>() {

    private var initialPageQuery: Query? = null

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, SellerBasicDto> {
        return try {
            // Construct query
            initialPageQuery = initialPageQuery ?: buildSellersPagingQuery(params.loadSize)

            val currentPageQuery = params.key ?: initialPageQuery
            val currentPageData = currentPageQuery!!.get().await()

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
                data = currentPageData.toObjects(SellerBasicDto::class.java),
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Query, SellerBasicDto>): Query? = initialPageQuery

    private fun buildSellersPagingQuery(pageSize: Int): Query {
        var pageQuery = firestore.collection(SELLERS_BASIC_COLLECTION).limit(pageSize.toLong())

        // Apply type filter
        sellerBasicsFilter.sellerType?.let {
            pageQuery = pageQuery.whereEqualTo(SELLER_TYPE_FIELD, it.ordinal)
        }

        // Apply category filter
        sellerBasicsFilter.categoryTag?.let {
            pageQuery = pageQuery.whereArrayContains(SELLER_TAGS_FIELD, it)
        }

        return pageQuery
    }
}