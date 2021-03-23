package com.foobarust.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.foobarust.data.constants.Constants.SELLER_SECTIONS_BASIC_SUB_COLLECTION
import com.foobarust.data.constants.Constants.SELLER_SECTION_AVAILABLE_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_CUTOFF_TIME_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_SELLER_ID_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_SELLER_NAME_FIELD
import com.foobarust.data.constants.Constants.SELLER_SECTION_STATE_AVAILABLE
import com.foobarust.data.constants.Constants.SELLER_SECTION_STATE_FIELD
import com.foobarust.data.models.seller.SellerSectionBasicDto
import com.foobarust.data.utils.isNetworkData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.*

/**
 * Created by kevin on 12/20/20
 * @param sellerId if sellerId is null, get all sections
 */

class SellerSectionsBasicPagingSource(
    private val firestore: FirebaseFirestore,
    private val sellerId: String? = null
) : PagingSource<Query, SellerSectionBasicDto>() {

    private var initialPageQuery: Query? = null

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, SellerSectionBasicDto> {
        return try {
            initialPageQuery = initialPageQuery ?: firestore.collectionGroup(SELLER_SECTIONS_BASIC_SUB_COLLECTION)
                .whereEqualTo(SELLER_SECTION_AVAILABLE_FIELD, true)
                .whereEqualTo(SELLER_SECTION_STATE_FIELD, SELLER_SECTION_STATE_AVAILABLE)
                .whereGreaterThan(SELLER_SECTION_CUTOFF_TIME_FIELD, Date())

            if (sellerId != null) {
                initialPageQuery!!.whereEqualTo(SELLER_SECTION_SELLER_ID_FIELD, sellerId)
            }

            initialPageQuery!!.orderBy(SELLER_SECTION_CUTOFF_TIME_FIELD, Query.Direction.ASCENDING)
                .orderBy(SELLER_SECTION_SELLER_NAME_FIELD, Query.Direction.ASCENDING)
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
                data = currentPageData.toObjects(SellerSectionBasicDto::class.java),
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Query, SellerSectionBasicDto>): Query? = initialPageQuery
}