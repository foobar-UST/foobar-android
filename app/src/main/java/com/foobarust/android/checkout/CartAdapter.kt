package com.foobarust.android.checkout

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.checkout.CartAdapter.*
import com.foobarust.android.checkout.CartListModel.*
import com.foobarust.android.checkout.CartViewHolder.*
import com.foobarust.android.databinding.*
import com.foobarust.android.utils.drawableFitVertical
import com.foobarust.android.utils.getColorCompat
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.android.utils.setSrc
import com.foobarust.domain.models.cart.*
import com.foobarust.domain.models.seller.SellerType
import com.foobarust.domain.utils.format
import com.foobarust.domain.utils.getTimeBy12Hour
import java.util.*

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
                infoItemModel = getItem(position) as CartInfoItemModel
            )
            is CartPurchaseItemViewHolder -> bindCartPurchaseItem(
                binding = holder.binding,
                purchaseItemModel = getItem(position) as CartPurchaseItemModel
            )
            is CartTotalPriceItemViewHolder -> bindCartTotalPriceItem(
                binding = holder.binding,
                totalPriceItemModel = getItem(position) as CartTotalPriceItemModel
            )
            is CartOrderNotesItemViewHolder -> bindCartOrderNotesItem(
                binding = holder.binding,
                orderNotesItemModel = getItem(position) as CartOrderNotesItemModel
            )
            is CartPurchaseSubtitleItemViewHolder -> bindCartPurchaseSubtitleItem(
                binding = holder.binding,
                purchaseSubtitleItemModel = getItem(position) as CartPurchaseSubtitleItemModel
            )
            is CartPurchaseActionsItemViewHolder -> bindCartPurchaseActionsItem(
                binding = holder.binding,
                purchaseActionsItemModel = getItem(position) as CartPurchaseActionsItemModel
            )
            is CartEmptyItemViewHolder -> bindCartEmptyItem(
                binding = holder.binding
            )
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
        infoItemModel: CartInfoItemModel
    ) = binding.run {
        with(sellerOfflineNoticeBanner.root) {
            isSelected = true // Enable moving text
            isVisible = !infoItemModel.sellerOnline
        }

        with(sellerImageView) {
            contentDescription = infoItemModel.cartTitle
            loadGlideUrl(
                imageUrl = infoItemModel.cartImageUrl,
                centerCrop = true,
                placeholder = R.drawable.placeholder_card
            )
        }

        with(sectionNav) {
            iconImageView.setSrc(R.drawable.ic_today)

            titleTextView.text = root.context.getString(
                R.string.cart_info_nav_subtitle_section,
                infoItemModel.sellerName
            )

            contentTextView.text = root.context.getString(
                R.string.cart_info_nav_format_section,
                infoItemModel.cartDeliveryTime?.format("yyyy-MM-dd"),
                infoItemModel.cartDeliveryTime?.getTimeBy12Hour()
            )

            root.isVisible = infoItemModel.sectionId != null // Show only for section cart
        }

        with(miscNav) {
            iconImageView.setSrc(R.drawable.ic_location_on)

            val titleRes = when (infoItemModel.sellerType) {
                SellerType.ON_CAMPUS -> R.string.cart_info_nav_subtitle_misc_on_campus
                SellerType.OFF_CAMPUS -> R.string.cart_info_nav_subtitle_misc_off_campus
            }
            titleTextView.text = root.context.getString(titleRes)

            contentTextView.text = infoItemModel.cartPickupAddress
        }

        // Navigate to SellerSection
        if (infoItemModel.sectionId != null) {
            sectionNav.root.setOnClickListener {
                listener.onSectionOptionClicked(sectionId = infoItemModel.sectionId)
            }
        }

        // Navigate to SellerMisc
        miscNav.root.setOnClickListener {
            listener.onSellerMiscOptionClicked(
                sellerId = infoItemModel.sellerId
            )
        }

        // Setup offline banner
        with(sellerOfflineNoticeBanner.noticeTextView) {
            val context = root.context
            if (!infoItemModel.sellerOnline) {
                text = context.getString(R.string.seller_detail_offline_message)
                background = ColorDrawable(context.getColorCompat(R.color.grey_disabled))
            }
        }
    }

    private fun bindCartPurchaseItem(
        binding: CartPurchaseItemBinding,
        purchaseItemModel: CartPurchaseItemModel
    ) = binding.run {
        root.setOnClickListener {
            listener.onCartItemClicked(purchaseItemModel.userCartItem)
        }

        removeButton.setOnClickListener {
            listener.onRemoveCartItem(purchaseItemModel.userCartItem)
        }

        with(itemImageView) {
            val itemImageUrl = purchaseItemModel.userCartItem.itemImageUrl
            isVisible = itemImageUrl != null

            if (itemImageUrl != null) {
                loadGlideUrl(
                    imageUrl = itemImageUrl,
                    centerCrop = true,
                    placeholder = R.drawable.placeholder_card
                )
            }
        }

        titleTextView.text = purchaseItemModel.userCartItem.getNormalizedTitle()

        quantityTextView.text = root.context.getString(
            R.string.cart_purchase_item_format_amount,
            purchaseItemModel.userCartItem.amounts
        )

        priceTextView.text = root.context.getString(
            R.string.cart_purchase_item_format_total_price,
            purchaseItemModel.userCartItem.totalPrice
        )

        with(unavailableTextView) {
            drawableFitVertical()
            isVisible = !purchaseItemModel.userCartItem.available
        }
    }

    private fun bindCartTotalPriceItem(
        binding: CartTotalPriceItemBinding,
        totalPriceItemModel: CartTotalPriceItemModel
    ) = binding.run {
        totalValueTextView.text = root.context.getString(
            R.string.cart_total_price_format_cost,
            totalPriceItemModel.total
        )

        subtotalValueTextView.text = root.context.getString(
            R.string.cart_total_price_format_cost,
            totalPriceItemModel.subtotal
        )

        deliveryFeeValueTextView.text = root.context.getString(
            R.string.cart_total_price_format_cost,
            totalPriceItemModel.deliveryFee
        )

    }

    private fun bindCartOrderNotesItem(
        binding: CartOrderNotesItemBinding,
        orderNotesItemModel: CartOrderNotesItemModel
    ) = binding.run {
        with(notesEditText) {
            setText(orderNotesItemModel.orderNotes)

            doOnTextChanged { text, _, _, _ ->
                listener.onUpdateOrderNotes(notes = text.toString())
            }

            // Clear focus after exit keyboard
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    notesEditText.clearFocus()
                }
                return@setOnEditorActionListener false
            }
        }
    }

    private fun bindCartPurchaseSubtitleItem(
        binding: SubtitleSmallItemBinding,
        purchaseSubtitleItemModel: CartPurchaseSubtitleItemModel
    ) = binding.run {
        subtitleTextView.text = purchaseSubtitleItemModel.subtitle
    }

    private fun bindCartPurchaseActionsItem(
        binding: CartPurchaseActionsItemBinding,
        purchaseActionsItemModel: CartPurchaseActionsItemModel
    ) = binding.run {
        addItemChip.setOnClickListener {
            listener.onAddMoreItemClicked(
                purchaseActionsItemModel.sellerId,
                purchaseActionsItemModel.sectionId
            )
        }

        removeItemsChip.setOnClickListener {
            listener.onClearCart()
        }
    }

    private fun bindCartEmptyItem(
        binding: EmptyListItemBinding
    ) = binding.run {
        val message = root.context.getString(R.string.cart_empty_message)

        with(emptyImageView) {
            setSrc(R.drawable.undraw_empty_cart)
            contentDescription = message
        }

        emptyMessageTextView.text = message
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
        val cartDeliveryTime: Date?,
        val sellerId: String,
        val sellerName: String,
        val sellerOnline: Boolean,
        val sellerType: SellerType,
        val sectionId: String?,
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

    object CartEmptyItemModel : CartListModel()
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