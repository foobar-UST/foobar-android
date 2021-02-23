package com.foobarust.android.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.OrderEmptyItemBinding
import com.foobarust.android.databinding.OrderHistoryArchivedItemBinding
import com.foobarust.android.databinding.OrderHistoryDeliveredItemBinding
import com.foobarust.android.databinding.SubtitleLargeItemBinding
import com.foobarust.android.order.OrderHistoryListModel.*
import com.foobarust.android.order.OrderHistoryViewHolder.*

/**
 * Created by kevin on 1/31/21
 */

class OrderHistoryAdapter(
    private val listener: OrderHistoryAdapterListener
) : PagingDataAdapter<OrderHistoryListModel, OrderHistoryViewHolder>(OrderHistoryListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.order_history_archived_item -> OrderHistoryArchivedItemViewHolder(
                OrderHistoryArchivedItemBinding.inflate(inflater, parent, false)
            )
            R.layout.order_history_delivered_item -> OrderHistoryDeliveredItemViewHolder(
                OrderHistoryDeliveredItemBinding.inflate(inflater, parent, false)
            )
            R.layout.order_empty_item -> OrderHistoryEmptyItemViewHolder(
                OrderEmptyItemBinding.inflate(inflater, parent, false)
            )
            R.layout.subtitle_large_item -> OrderHistorySubtitleItemViewHolder(
                SubtitleLargeItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        when (holder) {
            is OrderHistoryArchivedItemViewHolder -> holder.binding.run {
                historyItemModel = getItem(position) as OrderHistoryArchivedItemModel
                listener = this@OrderHistoryAdapter.listener
                executePendingBindings()
            }

            is OrderHistoryDeliveredItemViewHolder -> holder.binding.run {
                deliveredItemModel = getItem(position) as OrderHistoryDeliveredItemModel
                listener = this@OrderHistoryAdapter.listener
                executePendingBindings()
            }

            is OrderHistoryEmptyItemViewHolder -> holder.binding.run {
                emptyTitle = (getItem(position) as OrderHistoryEmptyItemModel).emptyTitle
                executePendingBindings()
            }

            is OrderHistorySubtitleItemViewHolder -> holder.binding.run {
                subtitle = (getItem(position) as OrderHistorySubtitleItemModel).subtitle
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OrderHistoryArchivedItemModel -> R.layout.order_history_archived_item
            is OrderHistoryDeliveredItemModel -> R.layout.order_history_delivered_item
            is OrderHistoryEmptyItemModel -> R.layout.order_empty_item
            is OrderHistorySubtitleItemModel -> R.layout.subtitle_large_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    interface OrderHistoryAdapterListener {
        fun onOrderClicked(orderId: String)
    }
}

sealed class OrderHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class OrderHistoryArchivedItemViewHolder(
        val binding: OrderHistoryArchivedItemBinding
    ) : OrderHistoryViewHolder(binding.root)

    class OrderHistoryDeliveredItemViewHolder(
        val binding: OrderHistoryDeliveredItemBinding
    ) : OrderHistoryViewHolder(binding.root)

    data class OrderHistoryEmptyItemViewHolder(
        val binding: OrderEmptyItemBinding
    ) : OrderHistoryViewHolder(binding.root)


    data class OrderHistorySubtitleItemViewHolder(
        val binding: SubtitleLargeItemBinding
    ) : OrderHistoryViewHolder(binding.root)
}

sealed class OrderHistoryListModel {
    data class OrderHistoryArchivedItemModel(
        val orderId: String,
        val orderIdentifierTitle: String,
        val orderDeliveryDate: String,
        val orderTotalCost: String,
        val orderImageUrl: String?
    ) : OrderHistoryListModel()

    data class OrderHistoryDeliveredItemModel(
        val orderId: String,
        val orderIdentifierTitle: String,
        val orderTitle: String,
        val orderImageUrl: String?
    ) : OrderHistoryListModel()

    data class OrderHistoryEmptyItemModel(
        val emptyTitle: String
    ) : OrderHistoryListModel()

    data class OrderHistorySubtitleItemModel(
        val subtitle: String
    ) : OrderHistoryListModel()
}

object OrderHistoryListModelDiff : DiffUtil.ItemCallback<OrderHistoryListModel>() {
    override fun areItemsTheSame(
        oldItem: OrderHistoryListModel,
        newItem: OrderHistoryListModel
    ): Boolean {
        return when {
            oldItem is OrderHistoryArchivedItemModel && newItem is OrderHistoryArchivedItemModel ->
                oldItem.orderId == newItem.orderId
            oldItem is OrderHistoryDeliveredItemModel && newItem is OrderHistoryDeliveredItemModel ->
                oldItem.orderId == newItem.orderId
            oldItem is OrderHistoryEmptyItemModel && newItem is OrderHistoryEmptyItemModel ->
                true
            oldItem is OrderHistorySubtitleItemModel && newItem is OrderHistorySubtitleItemModel ->
                oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: OrderHistoryListModel,
        newItem: OrderHistoryListModel
    ): Boolean {
        return when {
            oldItem is OrderHistoryArchivedItemModel && newItem is OrderHistoryArchivedItemModel ->
                oldItem == newItem
            oldItem is OrderHistoryDeliveredItemModel && newItem is OrderHistoryDeliveredItemModel ->
                oldItem == newItem
            oldItem is OrderHistoryEmptyItemModel && newItem is OrderHistoryEmptyItemModel ->
                true
            oldItem is OrderHistorySubtitleItemModel && newItem is OrderHistorySubtitleItemModel ->
                oldItem == newItem
            else -> false
        }
    }
}