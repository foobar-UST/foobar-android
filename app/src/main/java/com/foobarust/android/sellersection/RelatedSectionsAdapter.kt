package com.foobarust.android.sellersection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.RelatedSectionsExpandBinding
import com.foobarust.android.databinding.RelatedSectionsItemBinding
import com.foobarust.android.sellersection.RelatedSectionsListModel.*
import com.foobarust.android.sellersection.RelatedSectionsViewHolder.*
import java.util.*

/**
 * Created by kevin on 12/26/20
 */

class RelatedSectionsAdapter(
    private val sellerId: String,
    private val listener: RelatedSectionsAdapterListener
): ListAdapter<RelatedSectionsListModel, RelatedSectionsViewHolder>(
    RelatedSectionsListModelDiff
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedSectionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.related_sections_item -> RelatedSectionsItemViewHolder(
                RelatedSectionsItemBinding.inflate(inflater, parent, false)
            )

            R.layout.related_sections_expand -> RelatedSectionsExpandViewHolder(
                RelatedSectionsExpandBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RelatedSectionsViewHolder, position: Int) {
        when (holder) {
            is RelatedSectionsItemViewHolder -> holder.binding.run {
                sectionItem = getItem(position) as RelatedSectionsItemModel
                listener = this@RelatedSectionsAdapter.listener
                executePendingBindings()
            }

            is RelatedSectionsExpandViewHolder -> holder.binding.run {
                sellerId = this@RelatedSectionsAdapter.sellerId
                listener = this@RelatedSectionsAdapter.listener
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RelatedSectionsItemModel -> R.layout.related_sections_item
            is RelatedSectionsExpandModel -> R.layout.related_sections_expand
        }
    }

    override fun submitList(list: List<RelatedSectionsListModel>?) {
        // Add show more button
        val mergedList = if (list != null) list + RelatedSectionsExpandModel else list
        super.submitList(mergedList)
    }

    interface RelatedSectionsAdapterListener {
        fun onRelatedSectionClicked(sectionId: String)
        fun onExpandRelatedSections(sellerId: String)
    }
}

sealed class RelatedSectionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class RelatedSectionsItemViewHolder(
        val binding: RelatedSectionsItemBinding
    ) : RelatedSectionsViewHolder(binding.root)

    class RelatedSectionsExpandViewHolder(
        val binding: RelatedSectionsExpandBinding
    ) : RelatedSectionsViewHolder(binding.root)
}

sealed class RelatedSectionsListModel {
    data class RelatedSectionsItemModel(
        val sectionId: String,
        val sectionTitle: String,
        val sectionDeliveryTime: String,
        val sectionImageUrl: String?
    ) : RelatedSectionsListModel()

    object RelatedSectionsExpandModel : RelatedSectionsListModel()
}

object RelatedSectionsListModelDiff : DiffUtil.ItemCallback<RelatedSectionsListModel>() {
    override fun areItemsTheSame(
        oldItem: RelatedSectionsListModel,
        newItem: RelatedSectionsListModel
    ): Boolean {
        return when {
            oldItem is RelatedSectionsItemModel && newItem is RelatedSectionsItemModel ->
                oldItem.sectionId == newItem.sectionId
            oldItem is RelatedSectionsExpandModel && newItem is RelatedSectionsExpandModel ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: RelatedSectionsListModel,
        newItem: RelatedSectionsListModel
    ): Boolean {
        return when {
            oldItem is RelatedSectionsItemModel && newItem is RelatedSectionsItemModel ->
                oldItem == newItem
            oldItem is RelatedSectionsExpandModel && newItem is RelatedSectionsExpandModel ->
                true
            else -> false
        }
    }
}