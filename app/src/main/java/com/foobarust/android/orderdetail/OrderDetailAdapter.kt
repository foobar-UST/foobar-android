package com.foobarust.android.orderdetail

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
import com.foobarust.android.orderdetail.OrderDetailListModel.*
import com.foobarust.android.orderdetail.OrderDetailViewHolder.*
import com.foobarust.android.utils.buildColorStateListWith
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.android.utils.setDrawables
import com.foobarust.domain.models.order.OrderState
import com.foobarust.domain.models.order.OrderType
import com.foobarust.domain.utils.format
import java.util.*

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
            is OrderDetailHeaderItemViewHolder -> bindOrderDetailHeaderItem(
                binding = holder.binding,
                headerItemModel = getItem(position) as OrderDetailHeaderItemModel
            )
            is OrderDetailStateItemViewHolder -> bindOrderDetailStateItem(
                binding = holder.binding,
                stateItemModel = getItem(position) as OrderDetailStateItemModel
            )
            is OrderDetailInfoItemViewHolder -> bindOrderDetailInfoItem(
                binding = holder.binding,
                infoItemModel = getItem(position) as OrderDetailInfoItemModel
            )
            is OrderDetailPurchaseItemViewHolder -> bindOrderDetailPurchaseItem(
                binding = holder.binding,
                purchaseItemModel = getItem(position) as OrderDetailPurchaseItemModel
            )
            is OrderDetailCostItemViewHolder -> bindOrderDetailCostItem(
                binding = holder.binding,
                costItemModel = getItem(position) as OrderDetailCostItemModel
            )
            is OrderDetailPaymentItemViewHolder -> bindOrderDetailPaymentItem(
                binding = holder.binding,
                paymentItemModel = getItem(position) as OrderDetailPaymentItemModel
            )
            is OrderDetailActionsItemViewHolder -> bindOrderDetailActionsItem(
                binding = holder.binding
            )
        }
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

    private fun bindOrderDetailHeaderItem(
        binding: OrderDetailHeaderItemBinding,
        headerItemModel: OrderDetailHeaderItemModel
    ) = binding.run {
        deliveryAddressTitleTextView.text = when (headerItemModel.orderType) {
            OrderType.ON_CAMPUS -> root.context.getString(
                R.string.order_detail_header_item_delivery_address_title_on_campus
            )
            OrderType.OFF_CAMPUS -> root.context.getString(
                R.string.order_detail_header_item_delivery_address_title_off_campus
            )
        }

        deliveryAddressTextView.text = headerItemModel.deliveryAddress
    }

    private fun bindOrderDetailStateItem(
        binding: OrderDetailStateItemBinding,
        stateItemModel: OrderDetailStateItemModel
    ) = binding.run {
        val showProgressBar = stateItemModel.currentOrderState == stateItemModel.listOrderState &&
            stateItemModel.currentOrderState !in setOf(
                OrderState.DELIVERED, OrderState.ARCHIVED, OrderState.CANCELLED
            )

        // Set grey state icon for cancelled state, green otherwise.
        with(stateImageView) {
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

        loadingProgressBar.isVisible = showProgressBar

        with(pickUpVerifyButton) {
            isVisible = stateItemModel.listOrderState == OrderState.READY_FOR_PICK_UP &&
                stateItemModel.currentOrderState == OrderState.READY_FOR_PICK_UP
            setOnClickListener {
                listener.onPickupVerifyOrder()
            }
        }

        stateTitleTextView.text = stateItemModel.listStateTitle

        stateDescriptionTextView.text = stateItemModel.listStateDescription
    }

    private fun bindOrderDetailInfoItem(
        binding: OrderDetailInfoItemBinding,
        infoItemModel: OrderDetailInfoItemModel
    ) = binding.run {
        with(orderImageView) {
            val orderImageUrl = infoItemModel.orderItemImageUrl
            isVisible = orderImageUrl != null

            if (orderImageUrl != null) {
                loadGlideUrl(
                    imageUrl = orderImageUrl,
                    centerCrop = true,
                    placeholder = R.drawable.placeholder_card
                )
            }
        }

        identifierTitleTextView.text = root.context.getString(
            R.string.order_detail_info_item_identifier_title,
            infoItemModel.orderIdentifier
        )

        orderTitleTextView.text = infoItemModel.orderTitle

        createdAtTextView.text = root.context.getString(
            R.string.order_detail_info_item_created_at,
            infoItemModel.orderCreatedDate.format("yyyy-MM-dd HH:mm")
        )

        with(messageTextView) {
            isVisible = infoItemModel.orderMessage != null
            text = root.context.getString(
                R.string.order_detail_info_item_message,
                infoItemModel.orderMessage
            )
        }

        totalCostTextView.text = root.context.getString(
            R.string.order_detail_info_item_total_cost,
            infoItemModel.orderTotalCost
        )
    }

    private fun bindOrderDetailPurchaseItem(
        binding: OrderDetailPurchaseItemBinding,
        purchaseItemModel: OrderDetailPurchaseItemModel
    ) = binding.run {
        with(itemImageView) {
            val itemImageUrl = purchaseItemModel.orderItemImageUrl
            isVisible = itemImageUrl != null

            if (itemImageUrl != null) {
                loadGlideUrl(
                    imageUrl = itemImageUrl,
                    centerCrop = true,
                    placeholder = R.drawable.placeholder_card
                )
            }
        }

        itemTitleTextView.text = root.context.getString(
            R.string.order_detail_purchase_item_format_title,
            purchaseItemModel.orderItemTitle,
            purchaseItemModel.orderItemAmounts
        )

        itemPriceTextView.text = root.context.getString(
            R.string.order_detail_purchase_item_format_price,
            purchaseItemModel.orderItemTotalPrice
        )
    }

    private fun bindOrderDetailCostItem(
        binding: OrderDetailCostItemBinding,
        costItemModel: OrderDetailCostItemModel
    ) = binding.run {
        subtotalValueTextView.text = root.context.getString(
            R.string.order_detail_purchase_item_format_price,
            costItemModel.orderSubtotal
        )

        deliveryFeeValueTextView.text = root.context.getString(
            R.string.order_detail_purchase_item_format_price,
            costItemModel.orderDeliveryCost
        )
    }

    private fun bindOrderDetailPaymentItem(
        binding: OrderDetailPaymentItemBinding,
        paymentItemModel: OrderDetailPaymentItemModel
    ) = binding.run {
        with(paymentMethodTextView) {
            text = paymentItemModel.paymentMethodItem.title
            setDrawables(
                drawableLeft = paymentItemModel.paymentMethodItem.drawable
            )
        }
    }

    private fun bindOrderDetailActionsItem(
        binding: OrderDetailActionsItemBinding
    ) = binding.run {
        sellerContactButton.setOnClickListener {
            listener.onNavigateToSellerMisc()
        }
    }

    interface OrderDetailAdapterListener {
        fun onNavigateToSellerMisc()
        fun onPickupVerifyOrder()
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
        val orderType: OrderType,
        val deliveryAddress: String
    ) : OrderDetailListModel()

    data class OrderDetailStateItemModel(
        val currentOrderState: OrderState,
        val listOrderState: OrderState,
        val listStateTitle: String,
        val listStateDescription: String,
    ) : OrderDetailListModel()

    data class OrderDetailInfoItemModel(
        val orderIdentifier: String,
        val orderTitle: String,
        val orderCreatedDate: Date,
        val orderTotalCost: Double,
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