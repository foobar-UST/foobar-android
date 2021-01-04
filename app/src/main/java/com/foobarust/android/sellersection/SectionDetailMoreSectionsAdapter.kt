package com.foobarust.android.sellersection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SectionDetailMoreSectionsSectionItemBinding
import com.foobarust.android.databinding.SectionDetailMoreSectionsShowMoreItemBinding
import com.foobarust.android.sellersection.SectionDetailMoreSectionsListModel.*
import com.foobarust.android.sellersection.SectionDetailMoreSectionsViewHolder.*
import java.util.*

/**
 * Created by kevin on 12/26/20
 */

class SectionDetailMoreSectionsAdapter(
    private val sellerId: String,
    private val listener: SectionDetailMoreSectionsAdapterListener
): ListAdapter<SectionDetailMoreSectionsListModel, SectionDetailMoreSectionsViewHolder>(
    SectionDetailMoreSectionsListModelDiff
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionDetailMoreSectionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.section_detail_more_sections_section_item -> SectionDetailMoreSectionsSectionItemViewHolder(
                SectionDetailMoreSectionsSectionItemBinding.inflate(inflater, parent, false)
            )

            R.layout.section_detail_more_sections_show_more_item -> SectionDetailMoreSectionsShowMoreItemViewHolder(
                SectionDetailMoreSectionsShowMoreItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SectionDetailMoreSectionsViewHolder, position: Int) {
        when (holder) {
            is SectionDetailMoreSectionsSectionItemViewHolder -> holder.binding.run {
                sectionItem = getItem(position) as SectionDetailMoreSectionsSectionItem
                listener = this@SectionDetailMoreSectionsAdapter.listener
                executePendingBindings()
            }

            is SectionDetailMoreSectionsShowMoreItemViewHolder -> holder.binding.run {
                sellerId = this@SectionDetailMoreSectionsAdapter.sellerId
                listener = this@SectionDetailMoreSectionsAdapter.listener
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SectionDetailMoreSectionsSectionItem -> R.layout.section_detail_more_sections_section_item
            is SectionDetailMoreSectionsShowMoreItem -> R.layout.section_detail_more_sections_show_more_item
        }
    }

    override fun submitList(list: List<SectionDetailMoreSectionsListModel>?) {
        // Add show more button
        val mergedList = if (list != null) list + SectionDetailMoreSectionsShowMoreItem else list
        super.submitList(mergedList)
    }

    interface SectionDetailMoreSectionsAdapterListener {
        fun onSectionClicked(sectionId: String)
        fun onSectionsShowMoreClicked(sellerId: String)
    }
}

sealed class SectionDetailMoreSectionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SectionDetailMoreSectionsSectionItemViewHolder(
        val binding: SectionDetailMoreSectionsSectionItemBinding
    ) : SectionDetailMoreSectionsViewHolder(binding.root)

    class SectionDetailMoreSectionsShowMoreItemViewHolder(
        val binding: SectionDetailMoreSectionsShowMoreItemBinding
    ) : SectionDetailMoreSectionsViewHolder(binding.root)
}

sealed class SectionDetailMoreSectionsListModel {
    data class SectionDetailMoreSectionsSectionItem(
        val sectionId: String,
        val sectionTitle: String,
        val sectionDeliveryTime: String,
        val sectionImageUrl: String?
    ) : SectionDetailMoreSectionsListModel()

    object SectionDetailMoreSectionsShowMoreItem : SectionDetailMoreSectionsListModel()
}

object SectionDetailMoreSectionsListModelDiff : DiffUtil.ItemCallback<SectionDetailMoreSectionsListModel>() {
    override fun areItemsTheSame(
        oldItem: SectionDetailMoreSectionsListModel,
        newItem: SectionDetailMoreSectionsListModel
    ): Boolean {
        return when {
            oldItem is SectionDetailMoreSectionsSectionItem && newItem is SectionDetailMoreSectionsSectionItem ->
                oldItem.sectionId == newItem.sectionId
            oldItem is SectionDetailMoreSectionsShowMoreItem && newItem is SectionDetailMoreSectionsShowMoreItem ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SectionDetailMoreSectionsListModel,
        newItem: SectionDetailMoreSectionsListModel
    ): Boolean {
        return when {
            oldItem is SectionDetailMoreSectionsSectionItem && newItem is SectionDetailMoreSectionsSectionItem ->
                oldItem == newItem
            oldItem is SectionDetailMoreSectionsShowMoreItem && newItem is SectionDetailMoreSectionsShowMoreItem ->
                true
            else -> false
        }
    }
}