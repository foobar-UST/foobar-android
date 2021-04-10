package com.foobarust.android.seller

import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.EmptyListItemBinding
import com.foobarust.android.databinding.SellerItemBinding
import com.foobarust.android.databinding.SubtitleLargeItemBinding
import com.foobarust.android.seller.SellersListModel.*
import com.foobarust.android.seller.SellersViewHolder.*
import com.foobarust.android.utils.*
import com.foobarust.domain.models.seller.SellerType

/**
 * Created by kevin on 9/28/20
 */

class SellersAdapter(
    private val listener: SellersAdapterListener
) : PagingDataAdapter<SellersListModel, SellersViewHolder>(SellersListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.seller_item -> SellersItemViewHolder(
                SellerItemBinding.inflate(inflater, parent, false)
            )
            R.layout.subtitle_large_item -> SellersSubtitleViewHolder(
                SubtitleLargeItemBinding.inflate(inflater, parent, false)
            )
            R.layout.empty_list_item -> SellersEmptyViewHolder(
                EmptyListItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellersViewHolder, position: Int) {
        when (holder) {
            is SellersItemViewHolder -> bindSellerItem(
                binding = holder.binding,
                itemModel = getItem(position) as? SellersItemModel
            )
            is SellersSubtitleViewHolder -> bindSellerSubtitle(
                binding = holder.binding
            )
            is SellersEmptyViewHolder -> bindSellerEmpty(
                binding = holder.binding
            )
        }
    }

    private fun bindSellerItem(
        binding: SellerItemBinding,
        itemModel: SellersItemModel?
    ) = binding.run {
        if (itemModel == null) return@run

        root.setOnClickListener {
            listener.onSellerClicked(itemModel.sellerId, itemModel.sellerType)
        }

        // Setup photo layout round corner
        with(sellerImageLayout) {
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height,
                        resources.getDimension(
                            R.dimen.small_component_corner_radius
                        )
                    )
                }
            }
            clipToOutline = true
        }

        sellerOfflineTextView.isVisible = !itemModel.sellerOnline

        with(sellerImageView) {
            contentDescription = itemModel.sellerName
            loadGlideUrl(
                imageUrl = itemModel.sellerImageUrl,
                centerCrop = true,
                placeholder = R.drawable.placeholder_card
            )
        }

        nameTextView.text = itemModel.sellerName
        tagsTextView.text = itemModel.sellerTags

        with(statusTextView) {
            val statusColor = if (itemModel.sellerOnline) {
                context.themeColor(R.attr.colorSecondary)
            } else {
                context.getColorCompat(R.color.material_on_surface_disabled)
            }

            setTextColor(statusColor)

            text = if (itemModel.sellerOnline) {
                context.getString(R.string.seller_status_online)
            } else {
                context.getString(R.string.seller_status_offline)
            }
        }

        with(ratingTextView) {
            text = itemModel.sellerRating
            drawableFitVertical()
        }

        minSpendTextView.text = root.context.getString(
            R.string.seller_on_campus_item_min_spend,
            itemModel.sellerMinSpend
        )
    }

    private fun bindSellerSubtitle(
        binding: SubtitleLargeItemBinding
    ) = binding.run {
        subtitleTextView.text = root.context.getString(R.string.seller_subtitle)
    }

    private fun bindSellerEmpty(
        binding: EmptyListItemBinding
    ) = binding.run {
        emptyImageView.setSrc(R.drawable.undraw_empty)
        emptyMessageTextView.text = root.context.getString(R.string.seller_empty_message)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SellersItemModel -> R.layout.seller_item
            is SellersSubtitleModel -> R.layout.subtitle_large_item
            is SellersEmptyModel -> R.layout.empty_list_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    interface SellersAdapterListener {
        fun onSellerClicked(sellerId: String, sellerType: SellerType)
    }
}

sealed class SellersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellersItemViewHolder(
        val binding: SellerItemBinding
    ) : SellersViewHolder(binding.root)

    class SellersSubtitleViewHolder(
        val binding: SubtitleLargeItemBinding
    ) : SellersViewHolder(binding.root)

    class SellersEmptyViewHolder(
        val binding: EmptyListItemBinding
    ) : SellersViewHolder(binding.root)
}

sealed class SellersListModel {
    data class SellersItemModel(
        val sellerId: String,
        val sellerName: String,
        val sellerType: SellerType,
        val sellerImageUrl: String?,
        val sellerRating: String,
        val sellerMinSpend: Double,
        val sellerOnline: Boolean,
        val sellerTags: String,
    ) : SellersListModel()

    object SellersSubtitleModel : SellersListModel()

    object SellersEmptyModel : SellersListModel()
}

object SellersListModelDiff : DiffUtil.ItemCallback<SellersListModel>() {
    override fun areItemsTheSame(oldItem: SellersListModel, newItem: SellersListModel): Boolean {
        return when {
            oldItem is SellersItemModel && newItem is SellersItemModel ->
                oldItem.sellerId == newItem.sellerId
            oldItem is SellersSubtitleModel && newItem is SellersSubtitleModel ->
                true
            oldItem is SellersEmptyModel && newItem is SellersEmptyModel ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: SellersListModel, newItem: SellersListModel): Boolean {
        return when {
            oldItem is SellersItemModel && newItem is SellersItemModel ->
                oldItem == newItem
            oldItem is SellersSubtitleModel && newItem is SellersSubtitleModel ->
                true
            oldItem is SellersEmptyModel && newItem is SellersEmptyModel ->
                true
            else -> false
        }
    }
}
