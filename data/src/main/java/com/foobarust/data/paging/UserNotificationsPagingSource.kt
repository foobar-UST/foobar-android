package com.foobarust.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.foobarust.data.common.Constants.USERS_COLLECTION
import com.foobarust.data.common.Constants.USER_NOTIFICATIONS_SUB_COLLECTION
import com.foobarust.data.common.Constants.USER_NOTIFICATION_CREATED_AT_FIELD
import com.foobarust.data.models.user.UserNotificationDto
import com.foobarust.data.utils.isNetworkData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Created by kevin on 2/5/21
 */

class UserNotificationsPagingSource(
    private val firestore: FirebaseFirestore,
    private val userId: String
) : PagingSource<Query, UserNotificationDto>() {

    private var initialPageQuery: Query? = null

    override suspend fun load(params: LoadParams<Query>): LoadResult<Query, UserNotificationDto> {
        return try {
            initialPageQuery = initialPageQuery ?: firestore.collection(
                "$USERS_COLLECTION/$userId/$USER_NOTIFICATIONS_SUB_COLLECTION"
            )
                .orderBy(USER_NOTIFICATION_CREATED_AT_FIELD, Query.Direction.DESCENDING)
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
                data = currentPageData.toObjects(UserNotificationDto::class.java),
                prevKey = null,
                nextKey = nextPageQuery
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Query, UserNotificationDto>): Query? = initialPageQuery
}