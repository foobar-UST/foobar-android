package com.foobarust.android.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.*
import com.foobarust.android.order.OrderRecentListModel.*
import com.foobarust.android.order.OrderRecentViewHolder.*
import com.foobarust.domain.models.order.OrderState

/**
 * Created by kevin on 1/29/21
 */

class OrderRecentAdapter(
    private val listener: OrderRecentAdapterListener
) : ListAdapter<OrderRecentListModel, OrderRecentViewHolder>(OrderRecentListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderRecentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.order_recent_active_item -> OrderRecentActiveItemViewHolder(
                OrderRecentActiveItemBinding.inflate(inflater, parent, false)
            )
            R.layout.order_recent_delivered_item -> OrderRecentDeliveredItemViewHolder(
                OrderRecentDeliveredItemBinding.inflate(inflater, parent, false)
            )
            R.layout.order_empty_item -> OrderRecentEmptyItemViewHolder(
                OrderEmptyItemBinding.inflate(inflater, parent, false)
            )
            R.layout.subtitle_large_item -> OrderRecentSubtitleItemViewHolder(
                SubtitleLargeItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: OrderRecentViewHolder, position: Int) {
        when (holder) {
            is OrderRecentActiveItemViewHolder -> bindOrderRecentActiveItem(
                binding = holder.binding,
                activeItemModel = getItem(position) as OrderRecentActiveItemModel
            )

            is OrderRecentDeliveredItemViewHolder -> bindOrderRecentDeliveredItem(
                binding = holder.binding,
                deliveredItemModel = getItem(position) as OrderRecentDeliveredItemModel
            )

            is OrderRecentEmptyItemViewHolder -> holder.binding.run {
                emptyTitle = (getItem(position) as OrderRecentEmptyItemModel).emptyTitle
                executePendingBindings()
            }

            is OrderRecentSubtitleItemViewHolder -> holder.binding.run {
                subtitle = (getItem(position) as OrderRecentSubtitleItemModel).subtitle
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OrderRecentActiveItemModel -> R.layout.order_recent_active_item
            is OrderRecentDeliveredItemModel -> R.layout.order_recent_delivered_item
            is OrderRecentEmptyItemModel -> R.layout.order_empty_item
            is OrderRecentSubtitleItemModel -> R.layout.subtitle_large_item
        }
    }

    private fun bindOrderRecentActiveItem(
        binding: OrderRecentActiveItemBinding,
        activeItemModel: OrderRecentActiveItemModel
    ) = binding.run {
        // Active states: PROCESSING, PROCESSING, IN_TRANSIT, READY_FOR_PICK_UP
        this.activeItemModel = activeItemModel
        listener = this@OrderRecentAdapter.listener

        stateProgressBar.setProgressCompat(
            (activeItemModel.orderState.precedence + 1) * 25,
            true
        )

        executePendingBindings()
    }

    private fun bindOrderRecentDeliveredItem(
        binding: OrderRecentDeliveredItemBinding,
        deliveredItemModel: OrderRecentDeliveredItemModel
    ) = binding.run {
        // Delivered state: DELIVERED
        this.deliveredItemModel = deliveredItemModel
        listener = this@OrderRecentAdapter.listener

        /*
        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (fromUser) {
                this@OrderRecentAdapter.listener.onOrderRated(
                    orderId = deliveredItemModel.orderId,
                    rating = rating.toDouble()
                )
            }
        }

         */

        executePendingBindings()
    }

    interface OrderRecentAdapterListener {
        fun onOrderClicked(orderId: String)
        fun onOrderRated(orderId: String, rating: Double)
    }
}

sealed class OrderRecentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    data class OrderRecentActiveItemViewHolder(
        val binding: OrderRecentActiveItemBinding
    ) : OrderRecentViewHolder(binding.root)

    data class OrderRecentDeliveredItemViewHolder(
        val binding: OrderRecentDeliveredItemBinding
    ) : OrderRecentViewHolder(binding.root)

    data class OrderRecentEmptyItemViewHolder(
        val binding: OrderEmptyItemBinding
    ) : OrderRecentViewHolder(binding.root)

    data class OrderRecentSubtitleItemViewHolder(
        val binding: SubtitleLargeItemBinding
    ) : OrderRecentViewHolder(binding.root)
}

sealed class OrderRecentListModel {
    data class OrderRecentActiveItemModel(
        val orderId: String,
        val orderIdentifierTitle: String,
        val orderTitle: String,
        val orderDeliveryAddress: String,
        val orderImageUrl: String?,
        val orderState: OrderState,
        val orderUpdatedAt: String
    ) : OrderRecentListModel()

    data class OrderRecentDeliveredItemModel(
        val orderId: String,
        val orderIdentifierTitle: String,
        val orderTitle: String,
        val orderImageUrl: String?
    ) : OrderRecentListModel()

    data class OrderRecentEmptyItemModel(
        val emptyTitle: String
    ) : OrderRecentListModel()

    data class OrderRecentSubtitleItemModel(
        val subtitle: String
    ) : OrderRecentListModel()
}

object OrderRecentListModelDiff : DiffUtil.ItemCallback<OrderRecentListModel>() {
    override fun areItemsTheSame(
        oldItem: OrderRecentListModel,
        newItem: OrderRecentListModel
    ): Boolean {
        return when {
            oldItem is OrderRecentActiveItemModel && newItem is OrderRecentActiveItemModel ->
                oldItem.orderId == newItem.orderId
            oldItem is OrderRecentDeliveredItemModel && newItem is OrderRecentDeliveredItemModel ->
                oldItem.orderId == newItem.orderId
            oldItem is OrderRecentEmptyItemModel && newItem is OrderRecentEmptyItemModel ->
                true
            oldItem is OrderRecentSubtitleItemModel && newItem is OrderRecentSubtitleItemModel ->
                oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: OrderRecentListModel,
        newItem: OrderRecentListModel
    ): Boolean {
        return when {
            oldItem is OrderRecentActiveItemModel && newItem is OrderRecentActiveItemModel ->
                oldItem == newItem
            oldItem is OrderRecentDeliveredItemModel && newItem is OrderRecentDeliveredItemModel ->
                oldItem == newItem
            oldItem is OrderRecentEmptyItemModel && newItem is OrderRecentEmptyItemModel ->
                true
            oldItem is OrderRecentSubtitleItemModel && newItem is OrderRecentSubtitleItemModel ->
                oldItem == newItem
            else -> false
        }
    }
}