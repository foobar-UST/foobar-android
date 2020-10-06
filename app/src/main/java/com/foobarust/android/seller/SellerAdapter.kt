package com.foobarust.android.seller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SellerBasicItemBinding
import com.foobarust.android.databinding.SellerSubtitleItemBinding
import com.foobarust.android.seller.SellerAdapter.SellerAdapterListener
import com.foobarust.android.seller.SellerListModel.SellerBasicModel
import com.foobarust.android.seller.SellerListModel.SellerSubtitleModel
import com.foobarust.android.seller.SellerViewHolder.SellerBasicViewHolder
import com.foobarust.android.seller.SellerViewHolder.SellerSubtitleViewHolder
import com.foobarust.domain.models.SellerBasic

/**
 * Created by kevin on 9/28/20
 */

class SellerAdapter(
    private val listener: SellerAdapterListener
) : PagingDataAdapter<SellerListModel, SellerViewHolder>(SellerListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.seller_basic_item -> SellerBasicViewHolder(
                SellerBasicItemBinding.inflate(inflater, parent, false),
                listener
            )

            R.layout.seller_subtitle_item -> SellerSubtitleViewHolder(
                SellerSubtitleItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerViewHolder, position: Int) {
        when (holder) {
            is SellerBasicViewHolder -> holder.binding.run {
                sellerBasicItem = (getItem(position) as? SellerBasicModel)?.sellerBasic
                listener = this@SellerAdapter.listener
                executePendingBindings()
            }

            is SellerSubtitleViewHolder -> holder.binding.run {
                subtitle = (getItem(position) as? SellerSubtitleModel)?.subtitle
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SellerBasicModel -> R.layout.seller_basic_item
            is SellerSubtitleModel -> R.layout.seller_subtitle_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    interface SellerAdapterListener {
        fun onSellerListItemClicked(sellerBasic: SellerBasic)
        fun onSellerListItemLongClicked(view: View, sellerBasic: SellerBasic): Boolean
    }
}

sealed class SellerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerBasicViewHolder(
        val binding: SellerBasicItemBinding,
        val listener: SellerAdapterListener
    ) : SellerViewHolder(binding.root)

    class SellerSubtitleViewHolder(
        val binding: SellerSubtitleItemBinding
    ) : SellerViewHolder(binding.root)
}

sealed class SellerListModel {
    data class SellerBasicModel(
        val sellerBasic: SellerBasic
    ) : SellerListModel()

    data class SellerSubtitleModel(
        val subtitle: String
    ) : SellerListModel()
}

object SellerListModelDiff : DiffUtil.ItemCallback<SellerListModel>() {
    override fun areItemsTheSame(oldItem: SellerListModel, newItem: SellerListModel): Boolean {
        return when {
            oldItem is SellerBasicModel && newItem is SellerBasicModel -> oldItem.sellerBasic.id == newItem.sellerBasic.id
            oldItem is SellerSubtitleModel && newItem is SellerSubtitleModel -> oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: SellerListModel, newItem: SellerListModel): Boolean {
        return when {
            oldItem is SellerBasicModel && newItem is SellerBasicModel -> oldItem.sellerBasic == newItem.sellerBasic
            oldItem is SellerSubtitleModel && newItem is SellerSubtitleModel -> oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }
}

