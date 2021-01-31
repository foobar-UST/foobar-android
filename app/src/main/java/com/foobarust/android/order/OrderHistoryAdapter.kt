package com.foobarust.android.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.OrderEmptyItemBinding
import com.foobarust.android.databinding.OrderHistoryItemBinding
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
            R.layout.order_history_item -> OrderHistoryItemViewHolder(
                OrderHistoryItemBinding.inflate(inflater, parent, false)
            )

            R.layout.order_empty_item -> OrderHistoryEmptyItemViewHolder(
                OrderEmptyItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        when (holder) {
            is OrderHistoryItemViewHolder -> holder.binding.run {
                historyItemModel = getItem(position) as OrderHistoryItemModel
                listener = this@OrderHistoryAdapter.listener
                executePendingBindings()
            }

            is OrderHistoryEmptyItemViewHolder -> holder.binding.run {
                emptyTitle = (getItem(position) as OrderHistoryEmptyItemModel).emptyTitle
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OrderHistoryItemModel -> R.layout.order_history_item
            is OrderHistoryEmptyItemModel -> R.layout.order_empty_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    interface OrderHistoryAdapterListener {
        fun onOrderClicked(orderId: String)
    }
}

sealed class OrderHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class OrderHistoryItemViewHolder(
        val binding: OrderHistoryItemBinding
    ) : OrderHistoryViewHolder(binding.root)

    data class OrderHistoryEmptyItemViewHolder(
        val binding: OrderEmptyItemBinding
    ) : OrderHistoryViewHolder(binding.root)
}

sealed class OrderHistoryListModel {
    data class OrderHistoryItemModel(
        val orderId: String,
        val orderIdentifierTitle: String,
        val orderDeliveryDate: String,
        val orderTotalCost: String,
        val orderImageUrl: String?
    ) : OrderHistoryListModel()

    data class OrderHistoryEmptyItemModel(
        val emptyTitle: String
    ) : OrderHistoryListModel()
}

object OrderHistoryListModelDiff : DiffUtil.ItemCallback<OrderHistoryListModel>() {
    override fun areItemsTheSame(
        oldItem: OrderHistoryListModel,
        newItem: OrderHistoryListModel
    ): Boolean {
        return when {
            oldItem is OrderHistoryItemModel && newItem is OrderHistoryItemModel ->
                oldItem.orderId == newItem.orderId
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: OrderHistoryListModel,
        newItem: OrderHistoryListModel
    ): Boolean {
        return when {
            oldItem is OrderHistoryItemModel && newItem is OrderHistoryItemModel ->
                oldItem == newItem
            else -> false
        }
    }
}