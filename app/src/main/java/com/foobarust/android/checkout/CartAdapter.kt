package com.foobarust.android.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

/**
 * Created by kevin on 12/1/20
 */
class CartAdapter(
    private val listener: CartAdapterListener
) : ListAdapter<CartListModel, CartViewHolder>(CartListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.cart_info_item -> CartInfoItemViewHolder(
                CartInfoItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_purchase_item -> CartPurchaseItemViewHolder(
                CartPurchaseItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_total_price_item -> CartTotalPriceItemViewHolder(
                CartTotalPriceItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_order_notes_item -> CartOrderNotesItemViewHolder(
                CartOrderNotesItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_purchase_subtitle_item -> CartPurchaseSubtitleItemViewHolder(
                CartPurchaseSubtitleItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_purchase_actions_item -> CartPurchaseActionsItemViewHolder(
                CartPurchaseActionsItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        when (holder) {
            is CartInfoItemViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as CartInfoItemModel
                cartInfoItemModel = currentItem
                // Navigate to SellerSection
                if (currentItem.sectionId != null) {
                    sectionOption.itemOption.setOnClickListener {
                        listener.onSectionOptionClicked(
                            sellerId = currentItem.sellerId,
                            sectionId = currentItem.sectionId
                        )
                    }
                }
                // Navigate to SellerMisc
                miscOption.itemOption.setOnClickListener {
                    listener.onSellerMiscOptionClicked(
                        sellerId = currentItem.sellerId
                    )
                }
                executePendingBindings()
            }

            is CartPurchaseItemViewHolder -> holder.binding.run {
                purchaseItemModel = getItem(position) as CartPurchaseItemModel
                listener = this@CartAdapter.listener
                executePendingBindings()
            }

            is CartTotalPriceItemViewHolder -> holder.binding.run {
                totalPriceItemModel = getItem(position) as CartTotalPriceItemModel
                executePendingBindings()
            }

            is CartOrderNotesItemViewHolder -> holder.binding.run {
                orderNotesItemModel = getItem(position) as CartOrderNotesItemModel
                notesEditText.doOnTextChanged { text, _, _, _ ->
                    listener.onUpdateOrderNotes(notes = text.toString())
                }
                executePendingBindings()
            }

            is CartPurchaseSubtitleItemViewHolder -> holder.binding.run {
                subtitleItemModel = getItem(position) as CartPurchaseSubtitleItemModel
                executePendingBindings()
            }

            is CartPurchaseActionsItemViewHolder -> holder.binding.run {
                menuItemModel = getItem(position) as CartPurchaseActionsItemModel
                listener = this@CartAdapter.listener
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CartInfoItemModel -> R.layout.cart_info_item
            is CartPurchaseItemModel -> R.layout.cart_purchase_item
            is CartTotalPriceItemModel -> R.layout.cart_total_price_item
            is CartOrderNotesItemModel -> R.layout.cart_order_notes_item
            is CartPurchaseSubtitleItemModel -> R.layout.cart_purchase_subtitle_item
            is CartPurchaseActionsItemModel -> R.layout.cart_purchase_actions_item
        }
    }

    interface CartAdapterListener {
        fun onAddMoreItemClicked(sellerId: String, sectionId: String?)
        fun onSellerMiscOptionClicked(sellerId: String)
        fun onSectionOptionClicked(sellerId: String, sectionId: String?)
        fun onCartItemClicked(userCartItem: UserCartItem)
        fun onRemoveCartItem(userCartItem: UserCartItem)
        fun onClearCart()
        fun onUpdateOrderNotes(notes: String)
    }
}

sealed class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class CartInfoItemViewHolder(
        val binding: CartInfoItemBinding
    ) : CartViewHolder(binding.root)

    class CartPurchaseItemViewHolder(
        val binding: CartPurchaseItemBinding
    ) : CartViewHolder(binding.root)

    class CartTotalPriceItemViewHolder(
        val binding: CartTotalPriceItemBinding
    ) : CartViewHolder(binding.root)

    class CartOrderNotesItemViewHolder(
        val binding: CartOrderNotesItemBinding
    ) : CartViewHolder(binding.root)

    class CartPurchaseSubtitleItemViewHolder(
        val binding: CartPurchaseSubtitleItemBinding
    ) : CartViewHolder(binding.root)

    class CartPurchaseActionsItemViewHolder(
        val binding: CartPurchaseActionsItemBinding
    ) : CartViewHolder(binding.root)
}

sealed class CartListModel {
    data class CartInfoItemModel(
        val cartTitle: String,
        val cartImageUrl: String?,
        val cartPickupAddress: String,
        val cartDeliveryTime: String?,
        val sellerId: String,
        val sellerOnline: Boolean,
        val sectionId: String?
    ) : CartListModel()

    data class CartPurchaseItemModel(
        val userCartItem: UserCartItem
    ) : CartListModel()

    data class CartTotalPriceItemModel(
        val subtotal: Double,
        val deliveryFee: Double,
        val total: Double
    ) : CartListModel()

    data class CartOrderNotesItemModel(
        val orderNotes: String?
    ) : CartListModel()

    data class CartPurchaseSubtitleItemModel(
        val subtitle: String
    ) : CartListModel()

    data class CartPurchaseActionsItemModel(
        val sellerId: String,
        val sectionId: String?
    ) : CartListModel()
}

object CartListModelDiff : DiffUtil.ItemCallback<CartListModel>() {
    override fun areItemsTheSame(oldItem: CartListModel, newItem: CartListModel): Boolean {
        return when {
            oldItem is CartInfoItemModel && newItem is CartInfoItemModel ->
                true
            oldItem is CartPurchaseItemModel && newItem is CartPurchaseItemModel ->
                oldItem.userCartItem.id == newItem.userCartItem.id
            oldItem is CartTotalPriceItemModel && newItem is CartTotalPriceItemModel ->
                true
            oldItem is CartOrderNotesItemModel && newItem is CartOrderNotesItemModel ->
                true
            oldItem is CartPurchaseSubtitleItemModel && newItem is CartPurchaseSubtitleItemModel ->
                oldItem.subtitle == newItem.subtitle
            oldItem is CartPurchaseActionsItemModel && newItem is CartPurchaseActionsItemModel ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: CartListModel, newItem: CartListModel): Boolean {
        return when {
            oldItem is CartInfoItemModel && newItem is CartInfoItemModel ->
                oldItem == newItem
            oldItem is CartPurchaseItemModel && newItem is CartPurchaseItemModel ->
                oldItem.userCartItem == newItem.userCartItem
            oldItem is CartTotalPriceItemModel && newItem is CartTotalPriceItemModel ->
                oldItem == newItem
            oldItem is CartOrderNotesItemModel && newItem is CartOrderNotesItemModel ->
                oldItem == newItem
            oldItem is CartPurchaseSubtitleItemModel && newItem is CartPurchaseSubtitleItemModel ->
                oldItem == newItem
            oldItem is CartPurchaseActionsItemModel && newItem is CartPurchaseActionsItemModel ->
                true
            else -> false
        }
    }
}