package com.foobarust.android.seller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SellerOffCampusItemRecentBinding
import com.foobarust.android.databinding.SellerOffCampusItemUpcomingBinding
import com.foobarust.android.databinding.SubtitleItemBinding
import com.foobarust.android.seller.SellerOffCampusListModel.*
import com.foobarust.android.seller.SellerOffCampusViewHolder.*
import com.foobarust.domain.models.seller.SellerSectionBasic
import com.foobarust.domain.models.seller.isRecentSection

/**
 * Created by kevin on 12/21/20
 */

class SellerOffCampusAdapter(
    private val listener: SellerOffCampusAdapterListener
) : PagingDataAdapter<SellerOffCampusListModel, SellerOffCampusViewHolder>(SellerOffCampusListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerOffCampusViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.seller_off_campus_item_recent -> SellerOffCampusItemRecentViewHolder(
                SellerOffCampusItemRecentBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_off_campus_item_upcoming -> SellerOffCampusItemUpcomingViewHolder(
                SellerOffCampusItemUpcomingBinding.inflate(inflater, parent, false)
            )

            R.layout.subtitle_item -> SellerOffCampusSubtitleViewHolder(
                SubtitleItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerOffCampusViewHolder, position: Int) {
        when (holder) {
            is SellerOffCampusItemRecentViewHolder -> holder.binding.run {
                sectionBasic = (getItem(position) as? SellerOffCampusSectionModel)?.sellerSectionBasic
                listener = this@SellerOffCampusAdapter.listener
                executePendingBindings()
            }

            is SellerOffCampusItemUpcomingViewHolder -> holder.binding.run {
                sectionBasic = (getItem(position) as? SellerOffCampusSectionModel)?.sellerSectionBasic
                listener = this@SellerOffCampusAdapter.listener
                executePendingBindings()
            }

            is SellerOffCampusSubtitleViewHolder -> holder.binding.run {
                subtitle = (getItem(position) as? SellerOffCampusSubtitleModel)?.subtitle
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val currentItem = getItem(position)) {
            is SellerOffCampusSectionModel -> {
                if (currentItem.sellerSectionBasic.isRecentSection()) {
                    R.layout.seller_off_campus_item_recent
                } else {
                    R.layout.seller_off_campus_item_upcoming
                }
            }
            is SellerOffCampusSubtitleModel -> R.layout.subtitle_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    interface SellerOffCampusAdapterListener {
        fun onSellerSectionItemClicked(sectionBasic: SellerSectionBasic)
        fun onSellerSectionItemLongClicked(view: View, sectionBasic: SellerSectionBasic): Boolean
    }
}

sealed class SellerOffCampusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerOffCampusItemRecentViewHolder(
        val binding: SellerOffCampusItemRecentBinding
    ) : SellerOffCampusViewHolder(binding.root)

    class SellerOffCampusItemUpcomingViewHolder(
        val binding: SellerOffCampusItemUpcomingBinding
    ) : SellerOffCampusViewHolder(binding.root)

    class SellerOffCampusSubtitleViewHolder(
        val binding: SubtitleItemBinding
    ) : SellerOffCampusViewHolder(binding.root)
}

sealed class SellerOffCampusListModel {
    data class SellerOffCampusSectionModel(
        val sellerSectionBasic: SellerSectionBasic
    ) : SellerOffCampusListModel()

    data class SellerOffCampusSubtitleModel(
        val subtitle: String
    ) : SellerOffCampusListModel()
}

object SellerOffCampusListModelDiff : DiffUtil.ItemCallback<SellerOffCampusListModel>() {
    override fun areItemsTheSame(
        oldItem: SellerOffCampusListModel,
        newItem: SellerOffCampusListModel
    ): Boolean {
        return when {
            oldItem is SellerOffCampusSectionModel && newItem is SellerOffCampusSectionModel ->
                oldItem.sellerSectionBasic.id == newItem.sellerSectionBasic.id
            oldItem is SellerOffCampusSubtitleModel && newItem is SellerOffCampusSubtitleModel ->
                oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SellerOffCampusListModel,
        newItem: SellerOffCampusListModel
    ): Boolean {
        return when {
            oldItem is SellerOffCampusSectionModel && newItem is SellerOffCampusSectionModel ->
                oldItem.sellerSectionBasic == newItem.sellerSectionBasic
            oldItem is SellerOffCampusSubtitleModel && newItem is SellerOffCampusSubtitleModel ->
                oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }
}