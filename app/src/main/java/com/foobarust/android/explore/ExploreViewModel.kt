package com.foobarust.android.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.foobarust.android.explore.NotificationsListModel.*
import com.foobarust.android.utils.ResourceIdentifier
import com.foobarust.domain.usecases.user.GetUserNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by kevin on 2/14/21
 */

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val resourceIdentifier: ResourceIdentifier,
    getUserNotificationsUseCase: GetUserNotificationsUseCase
) : ViewModel() {

    val notificationListModels: Flow<PagingData<NotificationsListModel>> = getUserNotificationsUseCase(Unit)
        .map { pagingData ->
            pagingData.map {
                @Suppress("USELESS_CAST")
                NotificationsItemModel(
                    notificationId = it.id,
                    title = resourceIdentifier.getString(it.titleLocKey, it.titleLocArgs.toTypedArray()),
                    body = resourceIdentifier.getString(it.bodyLocKey, it.bodyLocArgs.toTypedArray()),
                    link = it.link,
                    imageUrl = it.imageUrl
                ) as NotificationsListModel
            }
        }
        .map { pagingData ->
            pagingData.insertSeparators { before, after ->
                return@insertSeparators if (before == null && after == null) {
                    NotificationEmptyModel
                } else {
                    null
                }
            }
        }
        .cachedIn(viewModelScope)
}