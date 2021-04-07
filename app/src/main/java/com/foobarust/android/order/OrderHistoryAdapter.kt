package com.foobarust.android.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.*
import com.foobarust.android.order.OrderHistoryListModel.*
import com.foobarust.android.order.OrderHistoryViewHolder.*
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.android.utils.setSrc
import com.foobarust.domain.models.order.*
import com.foobarust.domain.utils.format
import java.util.*

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
            R.layout.empty_list_item -> OrderHistoryEmptyItemViewHolder(
                EmptyListItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        when (holder) {
            is OrderHistoryItemViewHolder -> bindHistoryItem(
                binding = holder.binding,
                historyItemModel = getItem(position) as? OrderHistoryItemModel
            )

            is OrderHistoryEmptyItemViewHolder -> bindHistoryEmptyItem(
                binding = holder.binding
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OrderHistoryItemModel -> R.layout.order_history_item
            is OrderHistoryEmptyItemModel -> R.layout.empty_list_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    private fun bindHistoryItem(
        binding: OrderHistoryItemBinding,
        historyItemModel: OrderHistoryItemModel?
    ) = binding.run {
        if (historyItemModel == null) return@run

        orderHistoryCardView.setOnClickListener {
            when (historyItemModel.orderState) {
                OrderState.DELIVERED -> {
                    listener.onDeliveredOrderClickedClicked(historyItemModel.orderId)
                }
                OrderState.ARCHIVED -> {
                    listener.onArchivedOrderClicked(historyItemModel.orderId)
                }
                else -> throw IllegalStateException("Invalid state.")
            }
        }

        orderImageTitleTextView.text = when (historyItemModel.orderType) {
            OrderType.ON_CAMPUS -> historyItemModel.sellerName
            OrderType.OFF_CAMPUS -> root.context.getString(
                R.string.order_history_item_image_title,
                historyItemModel.sellerName,
                historyItemModel.orderTitle
            )
        }

        with(orderImageView) {
            val orderImageUrl = historyItemModel.orderImageUrl
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
            historyItemModel.orderIdentifier,
            historyItemModel.orderStateTitle
        )

        orderCreatedAtTextView.text = root.context.getString(
            R.string.order_item_created_at,
            historyItemModel.orderCreatedAt.format("yyyy-MM-dd HH:mm")
        )

        orderDeliveryLocationTextView.text = historyItemModel.orderDeliveryAddress

        orderTotalCostTextView.text = root.context.getString(
            R.string.order_item_total_cost,
            historyItemModel.orderTotalCost
        )

        showDetailTextView.text = when (historyItemModel.orderState) {
            OrderState.DELIVERED -> {
                root.context.getString(R.string.order_history_delivered_item_rate_order)
            }
            OrderState.ARCHIVED -> {
                root.context.getString(R.string.order_history_archived_item_view_order)
            }
            else -> throw IllegalStateException("Invalid order state.")
        }
    }

    private fun bindHistoryEmptyItem(
        binding: EmptyListItemBinding
    ) = binding.run {
        emptyImageView.setSrc(R.drawable.undraw_receipt)
        emptyMessageTextView.text = root.context.getString(R.string.order_history_empty_message)
    }

    interface OrderHistoryAdapterListener {
        fun onArchivedOrderClicked(orderId: String)
        fun onDeliveredOrderClickedClicked(orderId: String)
    }
}

sealed class OrderHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class OrderHistoryItemViewHolder(
        val binding: OrderHistoryItemBinding
    ) : OrderHistoryViewHolder(binding.root)

    data class OrderHistoryEmptyItemViewHolder(
        val binding: EmptyListItemBinding
    ) : OrderHistoryViewHolder(binding.root)
}

sealed class OrderHistoryListModel {
    data class OrderHistoryItemModel(
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
        val sellerName: String,
    ) : OrderHistoryListModel()

    object OrderHistoryEmptyItemModel : OrderHistoryListModel()
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
            else -> false
        }
    }
}