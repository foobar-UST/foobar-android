package com.foobarust.android.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.checkout.CartAdapter.*
import com.foobarust.android.checkout.CartListModel.*
import com.foobarust.android.checkout.CartViewHolder.*
import com.foobarust.android.databinding.*
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
            R.layout.cart_seller_info_item -> CartSellerInfoItemViewHolder(
                CartSellerInfoItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_purchase_item -> CartPurchaseItemViewHolder(
                CartPurchaseItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_total_price_item -> CartTotalPriceItemViewHolder(
                CartTotalPriceItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_actions_item -> CartActionsItemViewHolder(
                CartActionsItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_delivery_option_item -> CartDeliveryOptionItemViewHolder(
                CartDeliveryOptionItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_notes_item -> CartNotesItemViewHolder(
                CartNotesItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        when (holder) {
            is CartSellerInfoItemViewHolder -> holder.binding.run {
                sellerInfoModel = getItem(position) as CartSellerInfoItemModel
                listener = this@CartAdapter.listener
                executePendingBindings()
            }

            is CartPurchaseItemViewHolder -> holder.binding.run {
                purchaseItemModel = getItem(position) as CartPurchaseItemModel
                listener = this@CartAdapter.listener
                executePendingBindings()
            }

            is CartTotalPriceItemViewHolder -> holder.binding.run {
                totalPriceModel = getItem(position) as CartTotalPriceItemModel
                executePendingBindings()
            }

            is CartActionsItemViewHolder -> holder.binding.run {
                actionsModel = getItem(position) as CartActionsItemModel
                listener = this@CartAdapter.listener
                executePendingBindings()
            }

            is CartDeliveryOptionItemViewHolder -> holder.binding.run {
                deliveryOptionItemModel = getItem(position) as CartDeliveryOptionItemModel
                listener = this@CartAdapter.listener
                executePendingBindings()
            }

            is CartNotesItemViewHolder -> holder.binding.run {
                notesEditText.doOnTextChanged { text, _, _, _ ->
                    listener.onUpdateNotes(notes = text.toString())
                }
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CartSellerInfoItemModel -> R.layout.cart_seller_info_item
            is CartPurchaseItemModel -> R.layout.cart_purchase_item
            is CartTotalPriceItemModel -> R.layout.cart_total_price_item
            is CartActionsItemModel -> R.layout.cart_actions_item
            is CartDeliveryOptionItemModel -> R.layout.cart_delivery_option_item
            is CartNotesItemModel -> R.layout.cart_notes_item
        }
    }

    interface CartAdapterListener {
        fun onNavigateToSellerDetail(sellerId: String)
        fun onNavigateToSellerMisc(sellerId: String)
        fun onCartPurchaseItemClicked(userCartItem: UserCartItem)
        fun onRemoveCartItem(userCartItem: UserCartItem)
        fun onClearCart()
        fun onPlaceOrder()
        fun onUpdateNotes(notes: String)
        fun onChooseDeliveryOption()
    }
}

sealed class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class CartSellerInfoItemViewHolder(
        val binding: CartSellerInfoItemBinding
    ) : CartViewHolder(binding.root)

    class CartPurchaseItemViewHolder(
        val binding: CartPurchaseItemBinding
    ) : CartViewHolder(binding.root)

    class CartTotalPriceItemViewHolder(
        val binding: CartTotalPriceItemBinding
    ) : CartViewHolder(binding.root)

    class CartActionsItemViewHolder(
        val binding: CartActionsItemBinding
    ) : CartViewHolder(binding.root)

    class CartDeliveryOptionItemViewHolder(
        val binding: CartDeliveryOptionItemBinding
    ) : CartViewHolder(binding.root)

    class CartNotesItemViewHolder(
        val binding: CartNotesItemBinding
    ) : CartViewHolder(binding.root)
}

sealed class CartListModel {
    data class CartSellerInfoItemModel(
        val sellerBasic: SellerBasic
    ) : CartListModel()

    data class CartPurchaseItemModel(
        val userCartItem: UserCartItem
    ) : CartListModel()

    data class CartTotalPriceItemModel(
        val subtotal: Double,
        val deliveryFee: Double,
        val total: Double
    ) : CartListModel()

    data class CartActionsItemModel(
        val allowOrder: Boolean
    ) : CartListModel()

    data class CartDeliveryOptionItemModel(
        val optionId: String,
        val title: String,
        @DrawableRes val drawable: Int
    ) : CartListModel()

    object CartNotesItemModel : CartListModel()
}

object CartListModelDiff : DiffUtil.ItemCallback<CartListModel>() {
    override fun areItemsTheSame(oldItem: CartListModel, newItem: CartListModel): Boolean {
        return when {
            oldItem is CartSellerInfoItemModel && newItem is CartSellerInfoItemModel -> true
            oldItem is CartPurchaseItemModel && newItem is CartPurchaseItemModel ->
                oldItem.userCartItem.id == newItem.userCartItem.id
            oldItem is CartTotalPriceItemModel && newItem is CartTotalPriceItemModel -> true
            oldItem is CartActionsItemModel && newItem is CartActionsItemModel -> true
            oldItem is CartDeliveryOptionItemModel && newItem is CartDeliveryOptionItemModel -> true
            oldItem is CartNotesItemModel && newItem is CartNotesItemModel -> true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: CartListModel, newItem: CartListModel): Boolean {
        return when {
            oldItem is CartSellerInfoItemModel && newItem is CartSellerInfoItemModel ->
                oldItem.sellerBasic == newItem.sellerBasic
            oldItem is CartPurchaseItemModel && newItem is CartPurchaseItemModel ->
                oldItem.userCartItem == newItem.userCartItem
            oldItem is CartTotalPriceItemModel && newItem is CartTotalPriceItemModel -> oldItem == newItem
            oldItem is CartActionsItemModel && newItem is CartActionsItemModel -> oldItem == newItem
            oldItem is CartDeliveryOptionItemModel && newItem is CartDeliveryOptionItemModel -> oldItem == newItem
            oldItem is CartNotesItemModel && newItem is CartNotesItemModel -> oldItem == newItem
            else -> false
        }
    }
}