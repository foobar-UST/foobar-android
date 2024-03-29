package com.foobarust.android.explore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.ItemCategoryItemBinding
import com.foobarust.android.databinding.SubtitleLargeItemBinding
import com.foobarust.android.explore.ExploreListModel.ExploreItemCategoryItemModel
import com.foobarust.android.explore.ExploreListModel.ExploreSubtitleItemModel
import com.foobarust.android.explore.ExploreViewHolder.ExploreItemCategoryItemViewHolder
import com.foobarust.android.explore.ExploreViewHolder.ExploreSubtitleItemViewHolder
import com.foobarust.android.utils.loadGlideUrl

/**
 * Created by kevin on 2/26/21
 */

class ExploreAdapter(
    private val listener: ExploreAdapterListener
) : ListAdapter<ExploreListModel, ExploreViewHolder>(ExploreListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_category_item -> ExploreItemCategoryItemViewHolder(
                ItemCategoryItemBinding.inflate(inflater, parent, false)
            )
            R.layout.subtitle_large_item -> ExploreSubtitleItemViewHolder(
                SubtitleLargeItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        when (holder) {
            is ExploreItemCategoryItemViewHolder -> bindItemCategory(
                binding = holder.binding,
                itemCategoryItemModel = getItem(position) as ExploreItemCategoryItemModel
            )
            is ExploreSubtitleItemViewHolder -> bindSubtitleItem(
                binding = holder.binding
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ExploreItemCategoryItemModel -> R.layout.item_category_item
            is ExploreSubtitleItemModel -> R.layout.subtitle_large_item
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        // Use horizontal row for displaying subtitle (Given span count is 2).
        val gridLayoutManager = recyclerView.layoutManager as? GridLayoutManager
        gridLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (getItem(position)) {
                    is ExploreItemCategoryItemModel -> 1
                    is ExploreSubtitleItemModel -> 2
                }
            }
        }
    }

    private fun bindItemCategory(
        binding: ItemCategoryItemBinding,
        itemCategoryItemModel: ExploreItemCategoryItemModel
    ) = binding.run {
        root.setOnClickListener {
            listener.onItemCategoryClicked(itemCategoryItemModel.categoryId)
        }

        with(itemCategoryImageView) {
            contentDescription = itemCategoryItemModel.categoryTitle
            loadGlideUrl(
                imageUrl = itemCategoryItemModel.categoryImageUrl,
                centerCrop = true,
                placeholder = R.drawable.placeholder_card
            )
        }

        itemCategoryTitleTextView.text = itemCategoryItemModel.categoryTitle
    }

    private fun bindSubtitleItem(
        binding: SubtitleLargeItemBinding
    ) = binding.run {
        subtitleTextView.text = root.context.getString(R.string.explore_item_categories_subtitle)
    }

    interface ExploreAdapterListener {
        fun onItemCategoryClicked(categoryId: String)
    }
}

sealed class ExploreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class ExploreItemCategoryItemViewHolder(
        val binding: ItemCategoryItemBinding
    ) : ExploreViewHolder(binding.root)

    class ExploreSubtitleItemViewHolder(
        val binding: SubtitleLargeItemBinding
    ) : ExploreViewHolder(binding.root)
}

sealed class ExploreListModel {
    data class ExploreItemCategoryItemModel(
        val categoryId: String,
        val categoryTag: String,
        val categoryTitle: String,
        val categoryImageUrl: String?
    ) : ExploreListModel()

    object ExploreSubtitleItemModel : ExploreListModel()
}

object ExploreListModelDiff : DiffUtil.ItemCallback<ExploreListModel>() {
    override fun areItemsTheSame(
        oldItem: ExploreListModel,
        newItem: ExploreListModel
    ): Boolean {
        return when {
            oldItem is ExploreItemCategoryItemModel && newItem is ExploreItemCategoryItemModel ->
                oldItem.categoryId == newItem.categoryId
            oldItem is ExploreSubtitleItemModel && newItem is ExploreSubtitleItemModel ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: ExploreListModel,
        newItem: ExploreListModel
    ): Boolean {
        return when {
            oldItem is ExploreItemCategoryItemModel && newItem is ExploreItemCategoryItemModel ->
                oldItem == newItem
            oldItem is ExploreSubtitleItemModel && newItem is ExploreSubtitleItemModel ->
                oldItem == newItem
            else -> false
        }
    }
}