package com.foobarust.android.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
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
            R.layout.order_recent_item -> OrderRecentActiveItemViewHolder(
                OrderRecentItemBinding.inflate(inflater, parent, false)
            )
            R.layout.empty_list_item -> OrderRecentEmptyItemViewHolder(
                EmptyListItemBinding.inflate(inflater, parent, false)
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

            is OrderRecentEmptyItemViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as OrderRecentEmptyItemModel
                drawableRes = currentItem.drawableRes
                emptyMessage = currentItem.emptyMessage
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OrderRecentActiveItemModel -> R.layout.order_recent_item
            is OrderRecentEmptyItemModel -> R.layout.empty_list_item
        }
    }

    private fun bindOrderRecentActiveItem(
        binding: OrderRecentItemBinding,
        activeItemModel: OrderRecentActiveItemModel
    ) = binding.run {
        // Active states: PROCESSING, PROCESSING, IN_TRANSIT, READY_FOR_PICK_UP
        this.activeItemModel = activeItemModel
        listener = this@OrderRecentAdapter.listener

        /*
        stateProgressBar.setProgressCompat(
            activeItemModel.orderState.precedence * 25,
            true
        )

         */

        executePendingBindings()
    }

    interface OrderRecentAdapterListener {
        fun onOrderClicked(orderId: String)
    }
}

sealed class OrderRecentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    data class OrderRecentActiveItemViewHolder(
        val binding: OrderRecentItemBinding
    ) : OrderRecentViewHolder(binding.root)

    data class OrderRecentEmptyItemViewHolder(
        val binding: EmptyListItemBinding
    ) : OrderRecentViewHolder(binding.root)
}

sealed class OrderRecentListModel {
    data class OrderRecentActiveItemModel(
        val orderId: String,
        val orderImageTitle: String,
        val orderStateTitle: String,
        val orderDeliveryAddress: String,
        val orderTotalCost: Double,
        val orderImageUrl: String?,
        val orderState: OrderState,
        val orderUpdatedAt: String
    ) : OrderRecentListModel()

    data class OrderRecentEmptyItemModel(
        @DrawableRes val drawableRes: Int,
        val emptyMessage: String
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
            oldItem is OrderRecentEmptyItemModel && newItem is OrderRecentEmptyItemModel ->
                true
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
            oldItem is OrderRecentEmptyItemModel && newItem is OrderRecentEmptyItemModel ->
                true
            else -> false
        }
    }
}