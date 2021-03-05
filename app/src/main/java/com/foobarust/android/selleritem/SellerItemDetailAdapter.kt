package com.foobarust.android.selleritem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.*
import com.foobarust.android.selleritem.SellerItemDetailListModel.*
import com.foobarust.android.selleritem.SellerItemDetailViewHolder.*
import com.foobarust.domain.models.seller.SellerItemBasic

/**
 * Created by kevin on 1/19/21
 */

class SellerItemDetailAdapter(
    private val listener: SellerItemDetailAdapterListener
) : ListAdapter<SellerItemDetailListModel, SellerItemDetailViewHolder>(SellerItemDetailListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerItemDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.seller_item_detail_info_item -> SellerItemDetailInfoViewHolder(
                SellerItemDetailInfoItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_item_detail_description_item -> SellerItemDetailDescriptionViewHolder(
                SellerItemDetailDescriptionItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_item_detail_subtitle_item -> SellerItemDetailSubtitleViewHolder(
                SellerItemDetailSubtitleItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_item_detail_suggest_item -> SellerItemDetailSuggestViewHolder(
                SellerItemDetailSuggestItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerItemDetailViewHolder, position: Int) {
        when (holder) {
            is SellerItemDetailInfoViewHolder -> holder.binding.run {
                infoItemModel = (getItem(position)) as SellerItemDetailInfoItemModel
                executePendingBindings()
            }
            is SellerItemDetailDescriptionViewHolder -> holder.binding.run {
                descriptionItemModel = (getItem(position)) as SellerItemDetailDescriptionItemModel
                executePendingBindings()
            }
            is SellerItemDetailSubtitleViewHolder -> holder.binding.run {
                subtitleItemModel = (getItem(position)) as SellerItemDetailSubtitleItemModel
                executePendingBindings()
            }
            is SellerItemDetailSuggestViewHolder -> holder.binding.run {
                suggestItemModel = (getItem(position)) as SellerItemDetailSuggestItemModel
                listener = this@SellerItemDetailAdapter.listener
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SellerItemDetailInfoItemModel -> R.layout.seller_item_detail_info_item
            is SellerItemDetailDescriptionItemModel -> R.layout.seller_item_detail_description_item
            is SellerItemDetailSubtitleItemModel -> R.layout.seller_item_detail_subtitle_item
            is SellerItemDetailSuggestItemModel -> R.layout.seller_item_detail_suggest_item
        }
    }

    interface SellerItemDetailAdapterListener {
        fun onSuggestedItemClicked(itemBasic: SellerItemBasic)
        fun onSuggestedItemChecked(itemBasic: SellerItemBasic, isChecked: Boolean)
    }
}

sealed class SellerItemDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerItemDetailInfoViewHolder(
        val binding: SellerItemDetailInfoItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailDescriptionViewHolder(
        val binding: SellerItemDetailDescriptionItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailSubtitleViewHolder(
        val binding: SellerItemDetailSubtitleItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailSuggestViewHolder(
        val binding: SellerItemDetailSuggestItemBinding
    ) : SellerItemDetailViewHolder(binding.root)
}

sealed class SellerItemDetailListModel {
    data class SellerItemDetailInfoItemModel(
        val itemTitle: String,
        val itemPrice: Double
    ) : SellerItemDetailListModel()

    data class SellerItemDetailDescriptionItemModel(
        val itemDescription: String
    ) : SellerItemDetailListModel()

    data class SellerItemDetailSubtitleItemModel(
        val extraPrice: Double
    ) : SellerItemDetailListModel()

    data class SellerItemDetailSuggestItemModel(
        val itemBasic: SellerItemBasic,
    ) : SellerItemDetailListModel()
}

object SellerItemDetailListModelDiff : DiffUtil.ItemCallback<SellerItemDetailListModel>() {
    override fun areItemsTheSame(
        oldItem: SellerItemDetailListModel,
        newItem: SellerItemDetailListModel
    ): Boolean {
        return when {
            oldItem is SellerItemDetailInfoItemModel &&
                newItem is SellerItemDetailInfoItemModel ->
                true
            oldItem is SellerItemDetailDescriptionItemModel &&
                newItem is SellerItemDetailDescriptionItemModel ->
                true
            oldItem is SellerItemDetailSubtitleItemModel &&
                newItem is SellerItemDetailSubtitleItemModel ->
                true
            oldItem is SellerItemDetailSuggestItemModel &&
                newItem is SellerItemDetailSuggestItemModel ->
                oldItem.itemBasic.id == newItem.itemBasic.id
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SellerItemDetailListModel,
        newItem: SellerItemDetailListModel
    ): Boolean {
        return when {
            oldItem is SellerItemDetailInfoItemModel &&
                newItem is SellerItemDetailInfoItemModel -> oldItem == newItem
            oldItem is SellerItemDetailDescriptionItemModel &&
                newItem is SellerItemDetailDescriptionItemModel -> true
            oldItem is SellerItemDetailSubtitleItemModel &&
                newItem is SellerItemDetailSubtitleItemModel -> oldItem == newItem
            oldItem is SellerItemDetailSuggestItemModel &&
                newItem is SellerItemDetailSuggestItemModel -> oldItem == newItem
            else -> false
        }
    }
}