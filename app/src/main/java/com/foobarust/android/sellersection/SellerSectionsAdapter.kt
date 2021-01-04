package com.foobarust.android.sellersection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.*
import com.foobarust.android.sellersection.SellerSectionsListModel.*
import com.foobarust.android.sellersection.SellerSectionsViewHolder.*
import com.foobarust.domain.models.seller.SellerSectionBasic
import com.foobarust.domain.models.seller.isRecentSection

/**
 * Created by kevin on 12/21/20
 */

class SellerSectionsAdapter(
    private val listener: SellerOffCampusAdapterListener
) : PagingDataAdapter<SellerSectionsListModel, SellerSectionsViewHolder>(SellerSectionsListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerSectionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.seller_sections_item_recent -> SellerSectionsItemRecentViewHolder(
                SellerSectionsItemRecentBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_sections_item_upcoming -> SellerSectionsItemUpcomingViewHolder(
                SellerSectionsItemUpcomingBinding.inflate(inflater, parent, false)
            )

            R.layout.subtitle_item -> SellerSectionsSubtitleViewHolder(
                SubtitleItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerSectionsViewHolder, position: Int) {
        when (holder) {
            is SellerSectionsItemRecentViewHolder -> holder.binding.run {
                sectionBasic = (getItem(position) as? SellerSectionsItemModel)?.sellerSectionBasic
                listener = this@SellerSectionsAdapter.listener
                executePendingBindings()
            }

            is SellerSectionsItemUpcomingViewHolder -> holder.binding.run {
                sectionBasic = (getItem(position) as? SellerSectionsItemModel)?.sellerSectionBasic
                listener = this@SellerSectionsAdapter.listener
                executePendingBindings()
            }

            is SellerSectionsSubtitleViewHolder -> holder.binding.run {
                subtitle = (getItem(position) as? SellerSectionsSubtitleModel)?.subtitle
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val currentItem = getItem(position)) {
            is SellerSectionsItemModel -> {
                if (currentItem.sellerSectionBasic.isRecentSection()) {
                    R.layout.seller_sections_item_recent
                } else {
                    R.layout.seller_sections_item_upcoming
                }
            }
            is SellerSectionsSubtitleModel -> R.layout.subtitle_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    interface SellerOffCampusAdapterListener {
        fun onSellerSectionItemClicked(sectionBasic: SellerSectionBasic)
        fun onSellerSectionItemLongClicked(view: View, sectionBasic: SellerSectionBasic): Boolean
    }
}

sealed class SellerSectionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerSectionsItemRecentViewHolder(
        val binding: SellerSectionsItemRecentBinding
    ) : SellerSectionsViewHolder(binding.root)

    class SellerSectionsItemUpcomingViewHolder(
        val binding:  SellerSectionsItemUpcomingBinding
    ) : SellerSectionsViewHolder(binding.root)

    class SellerSectionsSubtitleViewHolder(
        val binding: SubtitleItemBinding
    ) : SellerSectionsViewHolder(binding.root)
}

sealed class SellerSectionsListModel {
    data class SellerSectionsItemModel(
        val sellerSectionBasic: SellerSectionBasic
    ) : SellerSectionsListModel()

    data class SellerSectionsSubtitleModel(
        val subtitle: String
    ) : SellerSectionsListModel()
}

object SellerSectionsListModelDiff : DiffUtil.ItemCallback<SellerSectionsListModel>() {
    override fun areItemsTheSame(
        oldItem: SellerSectionsListModel,
        newItem: SellerSectionsListModel
    ): Boolean {
        return when {
            oldItem is SellerSectionsItemModel && newItem is SellerSectionsItemModel ->
                oldItem.sellerSectionBasic.id == newItem.sellerSectionBasic.id
            oldItem is SellerSectionsSubtitleModel && newItem is SellerSectionsSubtitleModel ->
                oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SellerSectionsListModel,
        newItem: SellerSectionsListModel
    ): Boolean {
        return when {
            oldItem is SellerSectionsItemModel && newItem is SellerSectionsItemModel ->
                oldItem.sellerSectionBasic == newItem.sellerSectionBasic
            oldItem is SellerSectionsSubtitleModel && newItem is SellerSectionsSubtitleModel ->
                oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }
}