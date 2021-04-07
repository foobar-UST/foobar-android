package com.foobarust.android.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.EmptyListItemBinding
import com.foobarust.android.databinding.OrderRecentItemBinding
import com.foobarust.android.order.OrderRecentListModel.OrderRecentActiveItemModel
import com.foobarust.android.order.OrderRecentListModel.OrderRecentEmptyItemModel
import com.foobarust.android.order.OrderRecentViewHolder.OrderRecentActiveItemViewHolder
import com.foobarust.android.order.OrderRecentViewHolder.OrderRecentEmptyItemViewHolder
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.android.utils.setSrc
import com.foobarust.domain.models.order.OrderState
import com.foobarust.domain.models.order.OrderType
import com.foobarust.domain.utils.format
import java.util.*

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
            is OrderRecentEmptyItemViewHolder -> bindOrderRecentEmptyItem(
                binding = holder.binding
            )
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
        orderRecentCardView.setOnClickListener {
            listener.onActiveOrderClicked(activeItemModel.orderId)
        }

        orderImageTitleTextView.text = when (activeItemModel.orderType) {
            OrderType.ON_CAMPUS -> activeItemModel.sellerName
            OrderType.OFF_CAMPUS -> root.context.getString(
                R.string.order_history_item_image_title,
                activeItemModel.sellerName,
                activeItemModel.orderTitle
            )
        }

        with(orderImageView) {
            val orderImageUrl = activeItemModel.orderImageUrl
            isVisible = orderImageUrl != null
            contentDescription = orderImageTitleTextView.text

            if (orderImageUrl != null) {
                loadGlideUrl(
                    imageUrl = orderImageUrl,
                    centerCrop = true,
                    placeholder = R.drawable.placeholder_card
                )
            }
        }

        orderStateTitleTextView.text = root.context.getString(
            R.string.order_item_identifier_title,
            activeItemModel.orderIdentifier,
            activeItemModel.orderStateTitle
        )

        orderCreatedAtTextView.text = root.context.getString(
            R.string.order_item_created_at,
            activeItemModel.orderCreatedAt.format("yyyy-MM-dd HH:mm")
        )

        orderDeliveryLocationTextView.text = activeItemModel.orderDeliveryAddress

        orderTotalCostTextView.text = root.context.getString(
            R.string.order_item_total_cost,
            activeItemModel.orderTotalCost
        )

        // Active states: PROCESSING, PROCESSING, IN_TRANSIT, READY_FOR_PICK_UP
        orderStateProgressBar.setProgressCompat(
            activeItemModel.orderState.precedence * 33 + 1,
            true
        )
    }

    private fun bindOrderRecentEmptyItem(
        binding: EmptyListItemBinding
    ) = binding.run {
        emptyImageView.setSrc(R.drawable.undraw_receipt)
        emptyMessageTextView.text = root.context.getString(R.string.order_active_empty_message)
    }

    interface OrderRecentAdapterListener {
        fun onActiveOrderClicked(orderId: String)
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
        val orderTitle: String,
        val orderType: OrderType,
        val orderIdentifier: String,
        val orderState: OrderState,
        val orderStateTitle: String,
        val orderDeliveryAddress: String,
        val orderTotalCost: Double,
        val orderImageUrl: String?,
        val orderCreatedAt: Date,
        val sellerName: String
    ) : OrderRecentListModel()

    object OrderRecentEmptyItemModel : OrderRecentListModel()
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