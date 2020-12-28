package com.foobarust.android.sellersection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.MoreSectionsSectionItemBinding
import com.foobarust.android.databinding.MoreSectionsShowMoreItemBinding
import com.foobarust.android.sellersection.MoreSectionsListModel.*
import com.foobarust.android.sellersection.MoreSectionsViewHolder.*
import java.util.*

/**
 * Created by kevin on 12/26/20
 */

class MoreSectionsAdapter(
    private val sellerId: String,
    private val listener: MoreSectionsAdapterListener
): ListAdapter<MoreSectionsListModel, MoreSectionsViewHolder>(
    MoreSectionsListModelDiff
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreSectionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.more_sections_section_item -> MoreSectionsSectionItemViewHolder(
                MoreSectionsSectionItemBinding.inflate(inflater, parent, false)
            )

            R.layout.more_sections_show_more_item -> MoreSectionsShowMoreItemViewHolder(
                MoreSectionsShowMoreItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: MoreSectionsViewHolder, position: Int) {
        when (holder) {
            is MoreSectionsSectionItemViewHolder -> holder.binding.run {
                sectionItem = getItem(position) as MoreSectionsSectionItem
                listener = this@MoreSectionsAdapter.listener
                executePendingBindings()
            }

            is MoreSectionsShowMoreItemViewHolder -> holder.binding.run {
                sellerId = this@MoreSectionsAdapter.sellerId
                listener = this@MoreSectionsAdapter.listener
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MoreSectionsSectionItem -> R.layout.more_sections_section_item
            is MoreSectionsShowMoreItem -> R.layout.more_sections_show_more_item
        }
    }

    interface MoreSectionsAdapterListener {
        fun onSectionClicked(sectionId: String)
        fun onSectionsShowMoreClicked(sellerId: String)
    }
}

sealed class MoreSectionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class MoreSectionsSectionItemViewHolder(
        val binding: MoreSectionsSectionItemBinding
    ) : MoreSectionsViewHolder(binding.root)

    class MoreSectionsShowMoreItemViewHolder(
        val binding: MoreSectionsShowMoreItemBinding
    ) : MoreSectionsViewHolder(binding.root)
}

sealed class MoreSectionsListModel {
    data class MoreSectionsSectionItem(
        val sectionId: String,
        val sectionTitle: String,
        val sectionDeliveryTime: String,
        val sectionImageUrl: String?
    ) : MoreSectionsListModel()

    object MoreSectionsShowMoreItem : MoreSectionsListModel()
}

object MoreSectionsListModelDiff : DiffUtil.ItemCallback<MoreSectionsListModel>() {
    override fun areItemsTheSame(
        oldItem: MoreSectionsListModel,
        newItem: MoreSectionsListModel
    ): Boolean {
        return when {
            oldItem is MoreSectionsSectionItem && newItem is MoreSectionsSectionItem ->
                oldItem.sectionId == newItem.sectionId
            oldItem is MoreSectionsShowMoreItem && newItem is MoreSectionsShowMoreItem ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: MoreSectionsListModel,
        newItem: MoreSectionsListModel
    ): Boolean {
        return when {
            oldItem is MoreSectionsSectionItem && newItem is MoreSectionsSectionItem ->
                oldItem == newItem
            oldItem is MoreSectionsShowMoreItem && newItem is MoreSectionsShowMoreItem ->
                true
            else -> false
        }
    }
}