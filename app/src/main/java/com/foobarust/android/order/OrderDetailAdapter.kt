package com.foobarust.android.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.checkout.PaymentMethodItem
import com.foobarust.android.databinding.*
import com.foobarust.android.order.OrderDetailListModel.*
import com.foobarust.android.order.OrderDetailViewHolder.*
import com.foobarust.android.utils.buildColorStateListWith
import com.foobarust.domain.models.order.OrderState

/**
 * Created by kevin on 2/1/21
 */

class OrderDetailAdapter(
    private val listener: OrderDetailAdapterListener
): ListAdapter<OrderDetailListModel, OrderDetailViewHolder>(OrderDetailListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.order_detail_header_item -> OrderDetailHeaderItemViewHolder(
                OrderDetailHeaderItemBinding.inflate(inflater, parent, false)
            )
            R.layout.order_detail_state_item -> OrderDetailStateItemViewHolder(
                OrderDetailStateItemBinding.inflate(inflater, parent, false)
            )
            R.layout.order_detail_info_item -> OrderDetailInfoItemViewHolder(
                OrderDetailInfoItemBinding.inflate(inflater, parent, false)
            )
            R.layout.order_detail_purchase_item -> OrderDetailPurchaseItemViewHolder(
                OrderDetailPurchaseItemBinding.inflate(inflater, parent, false)
            )
            R.layout.order_detail_cost_item -> OrderDetailCostItemViewHolder(
                OrderDetailCostItemBinding.inflate(inflater, parent, false)
            )
            R.layout.order_detail_payment_item -> OrderDetailPaymentItemViewHolder(
                OrderDetailPaymentItemBinding.inflate(inflater, parent, false)
            )
            R.layout.order_detail_actions_item -> OrderDetailActionsItemViewHolder(
                OrderDetailActionsItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        when (holder) {
            is OrderDetailHeaderItemViewHolder -> holder.binding.run {
                headerItemModel = getItem(position) as OrderDetailHeaderItemModel
                executePendingBindings()
            }
            is OrderDetailStateItemViewHolder -> bindOrderDetailStateItem(
                binding = holder.binding,
                stateItemModel = getItem(position) as OrderDetailStateItemModel
            )
            is OrderDetailInfoItemViewHolder -> holder.binding.run {
                infoItemModel = getItem(position) as OrderDetailInfoItemModel
                executePendingBindings()
            }
            is OrderDetailPurchaseItemViewHolder -> holder.binding.run {
                purchaseItemModel = getItem(position) as OrderDetailPurchaseItemModel
                executePendingBindings()
            }
            is OrderDetailCostItemViewHolder -> holder.binding.run {
                costItemModel = getItem(position) as OrderDetailCostItemModel
                executePendingBindings()
            }
            is OrderDetailPaymentItemViewHolder -> holder.binding.run {
                paymentItemModel = getItem(position) as OrderDetailPaymentItemModel
                executePendingBindings()
            }
            is OrderDetailActionsItemViewHolder -> holder.binding.run {
                actionsItemModel = getItem(position) as OrderDetailActionsItemModel
                listener = this@OrderDetailAdapter.listener
                executePendingBindings()
            }
        }
    }

    private fun bindOrderDetailStateItem(
        binding: OrderDetailStateItemBinding,
        stateItemModel: OrderDetailStateItemModel
    ) = binding.run {
        this.stateItemModel = stateItemModel

        val showProgressBar = stateItemModel.currentOrderState == stateItemModel.listOrderState &&
            stateItemModel.currentOrderState !in setOf(
                OrderState.DELIVERED, OrderState.ARCHIVED, OrderState.CANCELLED
            )

        // Set grey state icon for cancelled state, green otherwise.
        with(binding.stateImageView) {
            imageTintList = if (stateItemModel.currentOrderState == OrderState.CANCELLED) {
                context.buildColorStateListWith(R.color.material_on_surface_disabled)
            } else {
                context.buildColorStateListWith(R.color.mint_500)
            }

            backgroundTintList = if (stateItemModel.currentOrderState == OrderState.CANCELLED) {
                context.buildColorStateListWith(R.color.grey_disabled)
            } else {
                context.buildColorStateListWith(R.color.mint_100)
            }

            isVisible = !showProgressBar
        }

        with(binding.loadingProgressBar) {
            isVisible = showProgressBar
        }

        executePendingBindings()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OrderDetailHeaderItemModel -> R.layout.order_detail_header_item
            is OrderDetailStateItemModel -> R.layout.order_detail_state_item
            is OrderDetailInfoItemModel -> R.layout.order_detail_info_item
            is OrderDetailPurchaseItemModel -> R.layout.order_detail_purchase_item
            is OrderDetailCostItemModel -> R.layout.order_detail_cost_item
            is OrderDetailPaymentItemModel -> R.layout.order_detail_payment_item
            is OrderDetailActionsItemModel -> R.layout.order_detail_actions_item
        }
    }

    interface OrderDetailAdapterListener {
        fun onNavigateToSellerContact()
    }
}

sealed class OrderDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class OrderDetailHeaderItemViewHolder(
        val binding: OrderDetailHeaderItemBinding
    ) : OrderDetailViewHolder(binding.root)

    class OrderDetailStateItemViewHolder(
        val binding: OrderDetailStateItemBinding
    ) : OrderDetailViewHolder(binding.root)

    class OrderDetailInfoItemViewHolder(
        val binding: OrderDetailInfoItemBinding
    ) : OrderDetailViewHolder(binding.root)

    class OrderDetailPurchaseItemViewHolder(
        val binding: OrderDetailPurchaseItemBinding
    ) : OrderDetailViewHolder(binding.root)

    class OrderDetailCostItemViewHolder(
        val binding: OrderDetailCostItemBinding
    ) : OrderDetailViewHolder(binding.root)

    class OrderDetailPaymentItemViewHolder(
        val binding: OrderDetailPaymentItemBinding
    ) : OrderDetailViewHolder(binding.root)

    class OrderDetailActionsItemViewHolder(
        val binding: OrderDetailActionsItemBinding
    ) : OrderDetailViewHolder(binding.root)
}

sealed class OrderDetailListModel {
    data class OrderDetailHeaderItemModel(
        val deliveryAddressTitle: String,
        val deliveryAddress: String
    ) : OrderDetailListModel()

    data class OrderDetailStateItemModel(
        val currentOrderState: OrderState,
        val listOrderState: OrderState,
        val listStateTitle: String,
        val listStateDescription: String,
    ) : OrderDetailListModel()

    data class OrderDetailInfoItemModel(
        val orderIdentifierTitle: String,
        val orderTitle: String,
        val orderCreatedDate: String,
        val orderTotalCost: String,
        val orderMessage: String?,
        val orderItemImageUrl: String?
    ) : OrderDetailListModel()

    data class OrderDetailPurchaseItemModel(
        val orderItemId: String,
        val orderItemTitle: String,
        val orderItemAmounts: Int,
        val orderItemTotalPrice: Double,
        val orderItemImageUrl: String?
    ) : OrderDetailListModel()

    data class OrderDetailCostItemModel(
        val orderSubtotal: Double,
        val orderDeliveryCost: Double
    ) : OrderDetailListModel()

    data class OrderDetailPaymentItemModel(
        val paymentMethodItem: PaymentMethodItem
    ) : OrderDetailListModel()

    object OrderDetailActionsItemModel : OrderDetailListModel()
}

object OrderDetailListModelDiff : DiffUtil.ItemCallback<OrderDetailListModel>() {
    override fun areItemsTheSame(
        oldItem: OrderDetailListModel,
        newItem: OrderDetailListModel
    ): Boolean {
        return when {
            oldItem is OrderDetailHeaderItemModel && newItem is OrderDetailHeaderItemModel ->
                true
            oldItem is OrderDetailStateItemModel && newItem is OrderDetailStateItemModel ->
                oldItem.listOrderState == newItem.listOrderState
            oldItem is OrderDetailInfoItemModel && newItem is OrderDetailInfoItemModel ->
                true
            oldItem is OrderDetailPurchaseItemModel && newItem is OrderDetailPurchaseItemModel ->
                oldItem.orderItemId == newItem.orderItemId
            oldItem is OrderDetailCostItemModel && newItem is OrderDetailCostItemModel ->
                true
            oldItem is OrderDetailPaymentItemModel && newItem is OrderDetailPaymentItemModel ->
                true
            oldItem is OrderDetailActionsItemModel && newItem is OrderDetailActionsItemModel ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: OrderDetailListModel,
        newItem: OrderDetailListModel
    ): Boolean {
        return when {
            oldItem is OrderDetailHeaderItemModel && newItem is OrderDetailHeaderItemModel ->
                oldItem == newItem
            oldItem is OrderDetailStateItemModel && newItem is OrderDetailStateItemModel ->
                oldItem == newItem
            oldItem is OrderDetailInfoItemModel && newItem is OrderDetailInfoItemModel ->
                oldItem == newItem
            oldItem is OrderDetailPurchaseItemModel && newItem is OrderDetailPurchaseItemModel ->
                oldItem == newItem
            oldItem is OrderDetailCostItemModel && newItem is OrderDetailCostItemModel ->
                oldItem == newItem
            oldItem is OrderDetailPaymentItemModel && newItem is OrderDetailPaymentItemModel ->
                oldItem == newItem
            oldItem is OrderDetailActionsItemModel && newItem is OrderDetailActionsItemModel ->
                true
            else -> false
        }
    }
}