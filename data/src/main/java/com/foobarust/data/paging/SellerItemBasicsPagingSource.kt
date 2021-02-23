package com.foobarust.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.foobarust.data.constants.Constants.SELLERS_COLLECTION
import com.foobarust.data.constants.Constants.SELLER_ITEMS_BASIC_SUB_COLLECTION
import com.foobarust.data.constants.Constants.SELLER_ITEM_CATALOG_ID_FIELD
import com.foobarust.data.models.seller.SellerItemBasicDto
import com.foobarust.data.utils.isNetworkData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 10/5/20
 */

class SellerItemBasicsPagingSource(
    private val firestore: FirebaseFirestore,
    private val sellerId: String,
    private val catalogId: String
) : PagingSource<Query, SellerItemBasicDto>() {

    private var initialPageQuery: Query? = null

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, SellerItemBasicDto> {
        return try {
            initialPageQuery = initialPageQuery ?: firestore.collection(
                "$SELLERS_COLLECTION/$sellerId/$SELLER_ITEMS_BASIC_SUB_COLLECTION"
            )
                .whereEqualTo(SELLER_ITEM_CATALOG_ID_FIELD, catalogId)
                // TODO: For testing items greyed out
                //.whereGreaterThan(SELLER_ITEM_COUNT_FIELD, 0)           // Having remaining items
                //.whereEqualTo(SELLER_ITEM_AVAILABLE_FIELD, true)        // Get available items only
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
                data = currentPageData.toObjects(SellerItemBasicDto::class.java),
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Query, SellerItemBasicDto>): Query? = initialPageQuery
}