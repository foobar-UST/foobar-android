package com.foobarust.android.checkout

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import com.foobarust.android.utils.getColorCompat
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

            R.layout.subtitle_small_item -> CartPurchaseSubtitleItemViewHolder(
                SubtitleSmallItemBinding.inflate(inflater, parent, false)
            )

            R.layout.cart_purchase_actions_item -> CartPurchaseActionsItemViewHolder(
                CartPurchaseActionsItemBinding.inflate(inflater, parent, false)
            )

            R.layout.empty_list_item -> CartEmptyItemViewHolder(
                EmptyListItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        when (holder) {
            is CartInfoItemViewHolder -> bindCartInfoItem(
                binding = holder.binding,
                cartInfoItemModel = getItem(position) as CartInfoItemModel
            )

            is CartPurchaseItemViewHolder -> holder.binding.run {
                purchaseItemModel = getItem(position) as CartPurchaseItemModel
                listener = this@CartAdapter.listener
                executePendingBindings()
            }

            is CartTotalPriceItemViewHolder -> holder.binding.run {
                totalPriceItemModel = getItem(position) as CartTotalPriceItemModel
                executePendingBindings()
            }

            is CartOrderNotesItemViewHolder -> bindOrderNotesItem(
                binding = holder.binding,
                orderNotesItemModel = getItem(position) as CartOrderNotesItemModel
            )

            is CartPurchaseSubtitleItemViewHolder -> holder.binding.run {
                subtitle = (getItem(position) as CartPurchaseSubtitleItemModel).subtitle
                executePendingBindings()
            }

            is CartPurchaseActionsItemViewHolder -> holder.binding.run {
                menuItemModel = getItem(position) as CartPurchaseActionsItemModel
                listener = this@CartAdapter.listener
                executePendingBindings()
            }

            is CartEmptyItemViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as CartEmptyItemModel
                drawableRes = currentItem.drawableRes
                emptyMessage = currentItem.emptyMessage
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
            is CartPurchaseSubtitleItemModel -> R.layout.subtitle_small_item
            is CartPurchaseActionsItemModel -> R.layout.cart_purchase_actions_item
            is CartEmptyItemModel -> R.layout.empty_list_item
        }
    }

    private fun bindCartInfoItem(
        binding: CartInfoItemBinding,
        cartInfoItemModel: CartInfoItemModel
    ) = binding.run {
        this.cartInfoItemModel = cartInfoItemModel

        // Navigate to SellerSection
        if (cartInfoItemModel.sectionId != null) {
            sectionNav.navLayout.setOnClickListener {
                listener.onSectionOptionClicked(sectionId = cartInfoItemModel.sectionId)
            }
        }

        // Navigate to SellerMisc
        miscNav.navLayout.setOnClickListener {
            listener.onSellerMiscOptionClicked(
                sellerId = cartInfoItemModel.sellerId
            )
        }

        // Setup offline banner
        with(sellerOfflineNoticeBanner.noticeTextView) {
            val context = root.context
            if (!cartInfoItemModel.sellerOnline) {
                text = context.getString(
                    R.string.seller_detail_offline_message
                )
                background = ColorDrawable(
                    context.getColorCompat(R.color.grey_disabled)
                )
            }
        }

        executePendingBindings()
    }

    private fun bindOrderNotesItem(
        binding: CartOrderNotesItemBinding,
        orderNotesItemModel: CartOrderNotesItemModel
    ) = binding.run {
        this.orderNotesItemModel = orderNotesItemModel

        notesEditText.doOnTextChanged { text, _, _, _ ->
            listener.onUpdateOrderNotes(notes = text.toString())
        }

        // Clear focus after exit keyboard
        notesEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                notesEditText.clearFocus()
            }
            return@setOnEditorActionListener false
        }

        executePendingBindings()
    }

    interface CartAdapterListener {
        fun onAddMoreItemClicked(sellerId: String, sectionId: String?)
        fun onSellerMiscOptionClicked(sellerId: String)
        fun onSectionOptionClicked(sectionId: String)
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
        val binding: SubtitleSmallItemBinding
    ) : CartViewHolder(binding.root)

    class CartPurchaseActionsItemViewHolder(
        val binding: CartPurchaseActionsItemBinding
    ) : CartViewHolder(binding.root)

    class CartEmptyItemViewHolder(
        val binding: EmptyListItemBinding
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
        val sectionId: String?,
        val sectionNavSubtitle: String,
        val miscNavSubtitle: String
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

    data class CartEmptyItemModel(
        @DrawableRes val drawableRes: Int,
        val emptyMessage: String
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
            oldItem is CartEmptyItemModel && newItem is CartEmptyItemModel ->
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
            oldItem is CartEmptyItemModel && newItem is CartEmptyItemModel ->
                true
            else -> false
        }
    }
}