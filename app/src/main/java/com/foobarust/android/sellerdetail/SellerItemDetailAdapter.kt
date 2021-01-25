package com.foobarust.android.sellerdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SellerItemDetailDescriptionItemBinding
import com.foobarust.android.databinding.SellerItemDetailInfoItemBinding
import com.foobarust.android.databinding.SellerItemDetailMoreItemBinding
import com.foobarust.android.databinding.SellerItemDetailSubtitleItemBinding
import com.foobarust.android.sellerdetail.SellerItemDetailListModel.*
import com.foobarust.android.sellerdetail.SellerItemDetailViewHolder.*
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
            R.layout.seller_item_detail_info_item -> SellerItemDetailInfoItemViewHolder(
                SellerItemDetailInfoItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_item_detail_description_item -> SellerItemDetailDescriptionItemViewHolder(
                SellerItemDetailDescriptionItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_item_detail_subtitle_item -> SellerItemDetailSubtitleItemViewHolder(
                SellerItemDetailSubtitleItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_item_detail_more_item -> SellerItemDetailMoreItemViewHolder(
                SellerItemDetailMoreItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerItemDetailViewHolder, position: Int) {
        when (holder) {
            is SellerItemDetailInfoItemViewHolder -> holder.binding.run {
                infoItemModel = (getItem(position)) as SellerItemDetailInfoItemModel
                executePendingBindings()
            }
            is SellerItemDetailDescriptionItemViewHolder -> holder.binding.run {
                descriptionItemModel = (getItem(position)) as SellerItemDetailDescriptionItemModel
                executePendingBindings()
            }
            is SellerItemDetailSubtitleItemViewHolder -> holder.binding.run {
                subtitleItemModel = (getItem(position)) as SellerItemDetailSubtitleItemModel
                executePendingBindings()
            }
            is SellerItemDetailMoreItemViewHolder -> holder.binding.run {
                val currentItem = (getItem(position)) as SellerItemDetailMoreItemModel
                moreItemModel = currentItem
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
            is SellerItemDetailMoreItemModel -> R.layout.seller_item_detail_more_item
        }
    }

    interface SellerItemDetailAdapterListener {
        fun onMoreItemClicked(itemBasic: SellerItemBasic)
        fun onMoreItemCheckedChange(itemBasic: SellerItemBasic, isChecked: Boolean)
    }
}

sealed class SellerItemDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerItemDetailInfoItemViewHolder(
        val binding: SellerItemDetailInfoItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailDescriptionItemViewHolder(
        val binding: SellerItemDetailDescriptionItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailSubtitleItemViewHolder(
        val binding: SellerItemDetailSubtitleItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailMoreItemViewHolder(
        val binding: SellerItemDetailMoreItemBinding
    ) : SellerItemDetailViewHolder(binding.root)
}

sealed class SellerItemDetailListModel {
    data class SellerItemDetailInfoItemModel(
        val itemTitle: String,
        val itemPrice: Double,
        val itemCount: Int
    ) : SellerItemDetailListModel()

    data class SellerItemDetailDescriptionItemModel(
        val itemDescription: String
    ) : SellerItemDetailListModel()

    data class SellerItemDetailSubtitleItemModel(
        val extraPrice: Double
    ) : SellerItemDetailListModel()

    data class SellerItemDetailMoreItemModel(
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
                newItem is SellerItemDetailInfoItemModel -> true
            oldItem is SellerItemDetailDescriptionItemModel &&
                newItem is SellerItemDetailDescriptionItemModel -> true
            oldItem is SellerItemDetailSubtitleItemModel &&
                newItem is SellerItemDetailSubtitleItemModel -> true
            oldItem is SellerItemDetailMoreItemModel &&
                newItem is SellerItemDetailMoreItemModel -> oldItem.itemBasic.id == newItem.itemBasic.id
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
            oldItem is SellerItemDetailMoreItemModel &&
                newItem is SellerItemDetailMoreItemModel -> oldItem == newItem
            else -> false
        }
    }
}