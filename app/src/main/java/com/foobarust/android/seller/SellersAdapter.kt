package com.foobarust.android.seller

import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.DrawableRes
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.EmptyListItemBinding
import com.foobarust.android.databinding.SellerItemBinding
import com.foobarust.android.databinding.SubtitleLargeItemBinding
import com.foobarust.android.seller.SellersListModel.*
import com.foobarust.android.seller.SellersViewHolder.*
import com.foobarust.android.utils.getColorCompat
import com.foobarust.android.utils.themeColor

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
            is SellersItemViewHolder -> bindOnCampusItem(
                binding = holder.binding,
                onCampusItemModel = getItem(position) as? SellersItemModel
            )
            is SellersSubtitleViewHolder -> holder.binding.run {
                subtitle = (getItem(position) as? SellersSubtitleModel)?.subtitle
                executePendingBindings()
            }
            is SellersEmptyViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as? SellersEmptyModel
                drawableRes = currentItem?.drawableRes
                emptyMessage = currentItem?.emptyMessage
                executePendingBindings()
            }
        }
    }

    private fun bindOnCampusItem(
        binding: SellerItemBinding,
        onCampusItemModel: SellersItemModel?
    ) = binding.run {
        if (onCampusItemModel == null) return@run

        val context = root.context

        this.onCampusItemModel = onCampusItemModel
        listener = this@SellersAdapter.listener

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

        // Set tags
        tagsTextView.text = context.getString(R.string.seller_item_info)

        // Set online status
        with(statusTextView) {
            val statusColor = if (onCampusItemModel.sellerOnline) {
                context.themeColor(R.attr.colorSecondary)
            } else {
                context.getColorCompat(R.color.material_on_surface_disabled)
            }

            setTextColor(statusColor)

            text = if (onCampusItemModel.sellerOnline) {
                context.getString(R.string.seller_status_online)
            } else {
                context.getString(R.string.seller_status_offline)
            }
        }

        executePendingBindings()
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
        fun onSellerClicked(sellerId: String)
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
        val sellerImageUrl: String?,
        val sellerRating: String,
        val sellerMinSpend: String,
        val sellerOnline: Boolean,
        val sellerTags: String,
    ) : SellersListModel()

    data class SellersSubtitleModel(
        val subtitle: String
    ) : SellersListModel()

    data class SellersEmptyModel(
        @DrawableRes val drawableRes: Int,
        val emptyMessage: String
    ) : SellersListModel()
}

object SellersListModelDiff : DiffUtil.ItemCallback<SellersListModel>() {
    override fun areItemsTheSame(oldItem: SellersListModel, newItem: SellersListModel): Boolean {
        return when {
            oldItem is SellersItemModel && newItem is SellersItemModel ->
                oldItem.sellerId == newItem.sellerId
            oldItem is SellersSubtitleModel && newItem is SellersSubtitleModel ->
                oldItem.subtitle == newItem.subtitle
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
                oldItem == newItem
            oldItem is SellersEmptyModel && newItem is SellersEmptyModel ->
                true
            else -> false
        }
    }
}
