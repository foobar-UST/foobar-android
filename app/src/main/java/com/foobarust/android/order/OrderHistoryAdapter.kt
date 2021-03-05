package com.foobarust.android.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.*
import com.foobarust.android.order.OrderHistoryListModel.*
import com.foobarust.android.order.OrderHistoryViewHolder.*
import com.foobarust.android.utils.bindDrawables
import com.foobarust.domain.models.order.OrderState

/**
 * Created by kevin on 1/31/21
 */

class OrderHistoryAdapter(
    private val listener: OrderHistoryAdapterListener
) : PagingDataAdapter<OrderHistoryListModel, OrderHistoryViewHolder>(OrderHistoryListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.order_history_item -> OrderHistoryArchivedItemViewHolder(
                OrderHistoryItemBinding.inflate(inflater, parent, false)
            )
            R.layout.empty_list_item -> OrderHistoryEmptyItemViewHolder(
                EmptyListItemBinding.inflate(inflater, parent, false)
            )
            R.layout.subtitle_large_item -> OrderHistorySubtitleItemViewHolder(
                SubtitleLargeItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        when (holder) {
            is OrderHistoryArchivedItemViewHolder -> bindHistoryItem(
                binding = holder.binding,
                historyItemModel = getItem(position) as? OrderHistoryItemModel
            )

            is OrderHistoryEmptyItemViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as? OrderHistoryEmptyItemModel
                drawableRes = currentItem?.drawableRes
                emptyMessage = currentItem?.emptyMessage
                executePendingBindings()
            }

            is OrderHistorySubtitleItemViewHolder -> holder.binding.run {
                subtitle = (getItem(position) as? OrderHistorySubtitleItemModel)?.subtitle
                executePendingBindings()
            }
        }
    }

    private fun bindHistoryItem(
        binding: OrderHistoryItemBinding,
        historyItemModel: OrderHistoryItemModel?
    ) = binding.run {
        if (historyItemModel == null) return@run

        this.historyItemModel = historyItemModel

        val context = root.context

        when (historyItemModel.orderState) {
            OrderState.DELIVERED -> {
                orderHistoryCardView.setOnClickListener {
                    listener.onOrderDeliveredClicked(historyItemModel.orderId)
                }
            }
            OrderState.ARCHIVED -> {
                orderHistoryCardView.setOnClickListener {
                    listener.onOrderArchivedClicked(historyItemModel.orderId)
                }
            }
            else -> Unit
        }

        with(showDetailTextView) {
            when (historyItemModel.orderState) {
                OrderState.DELIVERED -> {
                    text = context.getString(R.string.order_history_delivered_item_rate_order)
                    bindDrawables(drawableLeft = R.drawable.ic_star)
                }
                OrderState.ARCHIVED -> {
                    text = context.getString(R.string.order_history_archived_item_view_order)
                    bindDrawables(drawableLeft = R.drawable.ic_arrow_forward)
                }
                else -> Unit
            }
        }

        executePendingBindings()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OrderHistoryItemModel -> R.layout.order_history_item
            is OrderHistoryEmptyItemModel -> R.layout.empty_list_item
            is OrderHistorySubtitleItemModel -> R.layout.subtitle_large_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    interface OrderHistoryAdapterListener {
        fun onOrderArchivedClicked(orderId: String)
        fun onOrderDeliveredClicked(orderId: String)
    }
}

sealed class OrderHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class OrderHistoryArchivedItemViewHolder(
        val binding: OrderHistoryItemBinding
    ) : OrderHistoryViewHolder(binding.root)

    data class OrderHistoryEmptyItemViewHolder(
        val binding: EmptyListItemBinding
    ) : OrderHistoryViewHolder(binding.root)


    data class OrderHistorySubtitleItemViewHolder(
        val binding: SubtitleLargeItemBinding
    ) : OrderHistoryViewHolder(binding.root)
}

sealed class OrderHistoryListModel {
    data class OrderHistoryItemModel(
        val orderId: String,
        val orderImageTitle: String,
        val orderStateTitle: String,
        val orderDeliveryAddress: String,
        val orderTotalCost: Double,
        val orderImageUrl: String?,
        val orderState: OrderState,
        val orderUpdatedAt: String
    ) : OrderHistoryListModel()

    data class OrderHistoryEmptyItemModel(
        @DrawableRes val drawableRes: Int,
        val emptyMessage: String
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
            oldItem is OrderHistoryItemModel && newItem is OrderHistoryItemModel ->
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
            oldItem is OrderHistoryItemModel && newItem is OrderHistoryItemModel ->
                oldItem == newItem
            oldItem is OrderHistoryEmptyItemModel && newItem is OrderHistoryEmptyItemModel ->
                true
            oldItem is OrderHistorySubtitleItemModel && newItem is OrderHistorySubtitleItemModel ->
                oldItem == newItem
            else -> false
        }
    }
}