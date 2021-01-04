package com.foobarust.android.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.cart.CartAdapter.*
import com.foobarust.android.cart.CartListModel.*
import com.foobarust.android.cart.CartViewHolder.*
import com.foobarust.android.databinding.CartActionsItemBinding
import com.foobarust.android.databinding.CartPurchaseItemBinding
import com.foobarust.android.databinding.CartSellerInfoItemBinding
import com.foobarust.android.databinding.CartTotalPriceItemBinding
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.models.seller.SellerBasic

/**
 * Created by kevin on 12/1/20
 */
class CartAdapter(
    private val listener: CartAdapterListener
) : ListAdapter<CartListModel, CartViewHolder>(CartListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.cart_seller_info_item -> CartSellerInfoViewHolder(
                CartSellerInfoItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_purchase_item -> CartPurchaseItemViewHolder(
                CartPurchaseItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_total_price_item -> CartTotalPriceViewHolder(
                CartTotalPriceItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_actions_item -> CartActionsViewHolder(
                CartActionsItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        when (holder) {
            is CartSellerInfoViewHolder -> holder.binding.run {
                sellerInfoModel = getItem(position) as CartSellerInfoModel
                listener = this@CartAdapter.listener
                executePendingBindings()
            }

            is CartPurchaseItemViewHolder -> holder.binding.run {
                purchaseItemModel = getItem(position) as CartPurchaseItemModel
                listener = this@CartAdapter.listener
                executePendingBindings()
            }

            is CartTotalPriceViewHolder -> holder.binding.run {
                totalPriceModel = getItem(position) as CartTotalPriceModel
                executePendingBindings()
            }

            is CartActionsViewHolder -> holder.binding.run {
                actionsModel = getItem(position) as CartActionsModel
                listener = this@CartAdapter.listener
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CartSellerInfoModel -> R.layout.cart_seller_info_item
            is CartPurchaseItemModel -> R.layout.cart_purchase_item
            is CartTotalPriceModel -> R.layout.cart_total_price_item
            is CartActionsModel -> R.layout.cart_actions_item
        }
    }

    interface CartAdapterListener {
        fun onNavigateToSellerDetail(sellerId: String)
        fun onNavigateToSellerMisc(sellerId: String)
        fun onCartPurchaseItemClicked(userCartItem: UserCartItem)
        fun onRemoveCartItem(userCartItem: UserCartItem)
        fun onClearCart()
        fun onPlaceOrder()
    }
}

sealed class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class CartSellerInfoViewHolder(
        val binding: CartSellerInfoItemBinding
    ) : CartViewHolder(binding.root)

    class CartPurchaseItemViewHolder(
        val binding: CartPurchaseItemBinding
    ) : CartViewHolder(binding.root)

    class CartTotalPriceViewHolder(
        val binding: CartTotalPriceItemBinding
    ) : CartViewHolder(binding.root)

    class CartActionsViewHolder(
        val binding: CartActionsItemBinding
    ) : CartViewHolder(binding.root)
}

sealed class CartListModel {
    data class CartSellerInfoModel(
        val sellerBasic: SellerBasic
    ) : CartListModel()

    data class CartPurchaseItemModel(
        val userCartItem: UserCartItem
    ) : CartListModel()

    data class CartTotalPriceModel(
        val subtotal: Double,
        val deliveryFee: Double,
        val total: Double
    ) : CartListModel()

    data class CartActionsModel(
        val allowOrder: Boolean
    ) : CartListModel()
}

object CartListModelDiff : DiffUtil.ItemCallback<CartListModel>() {
    override fun areItemsTheSame(oldItem: CartListModel, newItem: CartListModel): Boolean {
        return when {
            oldItem is CartSellerInfoModel && newItem is CartSellerInfoModel -> true
            oldItem is CartPurchaseItemModel && newItem is CartPurchaseItemModel ->
                oldItem.userCartItem.id == newItem.userCartItem.id
            oldItem is CartTotalPriceModel && newItem is CartTotalPriceModel -> true
            oldItem is CartActionsModel && newItem is CartActionsModel -> true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: CartListModel, newItem: CartListModel): Boolean {
        return when {
            oldItem is CartSellerInfoModel && newItem is CartSellerInfoModel ->
                oldItem.sellerBasic == newItem.sellerBasic
            oldItem is CartPurchaseItemModel && newItem is CartPurchaseItemModel ->
                oldItem.userCartItem == newItem.userCartItem
            oldItem is CartTotalPriceModel && newItem is CartTotalPriceModel ->
                oldItem == newItem
            oldItem is CartActionsModel && newItem is CartActionsModel ->
                oldItem == newItem
            else -> false
        }
    }
}