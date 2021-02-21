package com.foobarust.android.explore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.ExploreNotificationEmptyBinding
import com.foobarust.android.databinding.ExploreNotificationItemBinding
import com.foobarust.android.explore.NotificationsListModel.*
import com.foobarust.android.explore.NotificationsViewHolder.*

/**
 * Created by kevin on 2/14/21
 */

class NotificationsAdapter(
    private val listener: NotificationsAdapterListener
) : PagingDataAdapter<NotificationsListModel, NotificationsViewHolder>(NotificationsListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.explore_notification_item -> NotificationsItemViewHolder(
                ExploreNotificationItemBinding.inflate(inflater, parent, false)
            )
            R.layout.explore_notification_empty -> NotificationEmptyViewHolder(
                ExploreNotificationEmptyBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        when (holder) {
            is NotificationsItemViewHolder -> holder.binding.run {
                notificationsItemModel = getItem(position) as NotificationsItemModel
                listener = this@NotificationsAdapter.listener
                executePendingBindings()
            }
            is NotificationEmptyViewHolder -> holder.binding.run {
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NotificationsItemModel -> R.layout.explore_notification_item
            is NotificationEmptyModel -> R.layout.explore_notification_empty
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    interface NotificationsAdapterListener {
        fun onNotificationClicked(link: String)
    }
}

sealed class NotificationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class NotificationsItemViewHolder(
        val binding: ExploreNotificationItemBinding
    ) : NotificationsViewHolder(binding.root)

    class NotificationEmptyViewHolder(
        val binding: ExploreNotificationEmptyBinding
    ) : NotificationsViewHolder(binding.root)
}

sealed class NotificationsListModel {
    data class NotificationsItemModel(
        val notificationId: String,
        val title: String,
        val body: String,
        val link: String,
        val imageUrl: String?
    ) : NotificationsListModel()

    object NotificationEmptyModel : NotificationsListModel()
}

object NotificationsListModelDiff : DiffUtil.ItemCallback<NotificationsListModel>() {
    override fun areItemsTheSame(
        oldItem: NotificationsListModel,
        newItem: NotificationsListModel
    ): Boolean {
        return when {
            oldItem is NotificationsItemModel && newItem is NotificationsItemModel ->
                oldItem.notificationId == newItem.notificationId
            oldItem is NotificationEmptyModel && newItem is NotificationEmptyModel ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: NotificationsListModel,
        newItem: NotificationsListModel
    ): Boolean {
        return when {
            oldItem is NotificationsItemModel && newItem is NotificationsItemModel ->
                oldItem == newItem
            oldItem is NotificationEmptyModel && newItem is NotificationEmptyModel ->
                true
            else -> false
        }
    }
}

