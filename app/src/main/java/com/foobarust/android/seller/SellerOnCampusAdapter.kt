package com.foobarust.android.seller

import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SellerOnCampusItemBinding
import com.foobarust.android.databinding.SellerSubtitleItemBinding
import com.foobarust.android.seller.SellerOnCampusListModel.SellerOnCampusItemModel
import com.foobarust.android.seller.SellerOnCampusListModel.SellerOnCampusSubtitleModel
import com.foobarust.android.seller.SellerOnCampusViewHolder.SellerOnCampusItemViewHolder
import com.foobarust.android.seller.SellerOnCampusViewHolder.SellerOnCampusSubtitleViewHolder
import com.foobarust.domain.models.SellerBasic

/**
 * Created by kevin on 9/28/20
 */

class SellerOnCampusAdapter(
    private val listener: SellerOnCampusAdapterListener
) : PagingDataAdapter<SellerOnCampusListModel, SellerOnCampusViewHolder>(SellerOnCampusListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerOnCampusViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.seller_on_campus_item -> SellerOnCampusItemViewHolder(
                SellerOnCampusItemBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_subtitle_item -> SellerOnCampusSubtitleViewHolder(
                SellerSubtitleItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerOnCampusViewHolder, position: Int) {
        when (holder) {
            is SellerOnCampusItemViewHolder -> holder.binding.run {
                // Setup binding
                sellerBasic = (getItem(position) as? SellerOnCampusItemModel)?.sellerBasic
                listener = this@SellerOnCampusAdapter.listener

                // Setup photo layout round corner
                photoImageViewLayout.run {
                    outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View, outline: Outline) {
                            outline.setRoundRect(0, 0, view.width, view.height,
                                photoImageViewLayout.context.resources.getDimension(R.dimen.small_component_corner_radius)
                            )
                        }
                    }
                    clipToOutline = true
                }

                executePendingBindings()
            }

            is SellerOnCampusSubtitleViewHolder -> holder.binding.run {
                subtitle = (getItem(position) as? SellerOnCampusSubtitleModel)?.subtitle
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SellerOnCampusItemModel -> R.layout.seller_on_campus_item
            is SellerOnCampusSubtitleModel -> R.layout.seller_subtitle_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    interface SellerOnCampusAdapterListener {
        fun onSellerListItemClicked(sellerBasic: SellerBasic)
        fun onSellerListItemLongClicked(view: View, sellerBasic: SellerBasic): Boolean
    }
}

sealed class SellerOnCampusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerOnCampusItemViewHolder(
        val binding: SellerOnCampusItemBinding
    ) : SellerOnCampusViewHolder(binding.root)

    class SellerOnCampusSubtitleViewHolder(
        val binding: SellerSubtitleItemBinding
    ) : SellerOnCampusViewHolder(binding.root)
}

sealed class SellerOnCampusListModel {
    data class SellerOnCampusItemModel(
        val sellerBasic: SellerBasic
    ) : SellerOnCampusListModel()

    data class SellerOnCampusSubtitleModel(
        val subtitle: String
    ) : SellerOnCampusListModel()
}

object SellerOnCampusListModelDiff : DiffUtil.ItemCallback<SellerOnCampusListModel>() {
    override fun areItemsTheSame(oldItem: SellerOnCampusListModel, newItem: SellerOnCampusListModel): Boolean {
        return when {
            oldItem is SellerOnCampusItemModel && newItem is SellerOnCampusItemModel ->
                oldItem.sellerBasic.id == newItem.sellerBasic.id
            oldItem is SellerOnCampusSubtitleModel && newItem is SellerOnCampusSubtitleModel ->
                oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: SellerOnCampusListModel, newItem: SellerOnCampusListModel): Boolean {
        return when {
            oldItem is SellerOnCampusItemModel && newItem is SellerOnCampusItemModel ->
                oldItem.sellerBasic == newItem.sellerBasic
            oldItem is SellerOnCampusSubtitleModel && newItem is SellerOnCampusSubtitleModel ->
                oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }
}
