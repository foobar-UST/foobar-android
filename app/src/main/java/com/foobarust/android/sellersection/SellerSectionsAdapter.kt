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
import com.foobarust.android.utils.drawableFitVertical
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.android.utils.setSrc
import com.foobarust.domain.models.seller.*
import com.foobarust.domain.utils.format
import com.foobarust.domain.utils.getTimeBy12Hour

/**
 * Created by kevin on 12/21/20
 */

class SellerSectionsAdapter(
    private val listener: SellerSectionsAdapterListener
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

            R.layout.subtitle_large_item -> SellerSectionsSubtitleViewHolder(
                SubtitleLargeItemBinding.inflate(inflater, parent, false)
            )

            R.layout.empty_list_item -> SellerSectionEmptyViewHolder(
                EmptyListItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerSectionsViewHolder, position: Int) {
        when (holder) {
            is SellerSectionsItemRecentViewHolder -> bindSectionItemRecent(
                binding = holder.binding,
                sectionsItemModel = getItem(position) as? SellerSectionsItemModel
            )
            is SellerSectionsItemUpcomingViewHolder -> bindSectionItemUpcoming(
                binding = holder.binding,
                sectionsItemModel = getItem(position) as? SellerSectionsItemModel
            )
            is SellerSectionsSubtitleViewHolder -> bindSectionSubtitle(
                binding = holder.binding,
                subtitleModel = getItem(position) as? SellerSectionsSubtitleModel
            )
            is SellerSectionEmptyViewHolder -> bindSectionEmpty(
                binding = holder.binding
            )
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
            is SellerSectionsSubtitleModel -> R.layout.subtitle_large_item
            is SellerSectionsEmptyModel -> R.layout.empty_list_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    private fun bindSectionItemRecent(
        binding: SellerSectionsItemRecentBinding,
        sectionsItemModel: SellerSectionsItemModel?
    ) = binding.run {
        if (sectionsItemModel == null) return@run

        root.setOnClickListener {
            listener.onSellerSectionItemClicked(sectionsItemModel.sellerSectionBasic)
        }

        sectionImageView.loadGlideUrl(
            imageUrl = sectionsItemModel.sellerSectionBasic.imageUrl,
            centerCrop = true,
            placeholder = R.drawable.placeholder_card
        )

        titleTextView.text = sectionsItemModel.sellerSectionBasic.getNormalizedTitleForRecent()

        deliveryTimeTextView.text = root.context.getString(
            R.string.seller_section_cutoff_time,
            sectionsItemModel.sellerSectionBasic.cutoffTime.getTimeBy12Hour()
        )

        sellerNameTextView.text = sectionsItemModel.sellerSectionBasic.getSellerNormalizedName()

        with(usersCountTextView) {
            drawableFitVertical()
            text = context.getString(
                R.string.seller_section_user_counts,
                sectionsItemModel.sellerSectionBasic.joinedUsersCount,
                sectionsItemModel.sellerSectionBasic.maxUsers
            )
        }
    }

    private fun bindSectionItemUpcoming(
        binding: SellerSectionsItemUpcomingBinding,
        sectionsItemModel: SellerSectionsItemModel?
    ) = binding.run {
        if (sectionsItemModel == null) return@run

        root.setOnClickListener {
            listener.onSellerSectionItemClicked(sectionsItemModel.sellerSectionBasic)
        }

        sectionImageView.loadGlideUrl(
            imageUrl = sectionsItemModel.sellerSectionBasic.imageUrl,
            centerCrop = true,
            placeholder = R.drawable.placeholder_card
        )

        sellerNameTextView.text = sectionsItemModel.sellerSectionBasic.getSellerNormalizedName()

        titleTextView.text = sectionsItemModel.sellerSectionBasic.getNormalizedTitleForUpcoming()

        dateTextView.text = root.context.getString(
            R.string.seller_section_date,
            sectionsItemModel.sellerSectionBasic.deliveryTime.format("yyyy-MM-dd")
        )
    }

    private fun bindSectionSubtitle(
        binding: SubtitleLargeItemBinding,
        subtitleModel: SellerSectionsSubtitleModel?
    ) = binding.run {
        if (subtitleModel == null) return@run
        subtitleTextView.text = subtitleModel.subtitle
    }

    private fun bindSectionEmpty(
        binding: EmptyListItemBinding
    ) = binding.run {
        emptyImageView.setSrc(R.drawable.undraw_empty)
        emptyMessageTextView.text = root.context.getString(R.string.seller_section_empty_message)
    }

    interface SellerSectionsAdapterListener {
        fun onSellerSectionItemClicked(sectionBasic: SellerSectionBasic)
    }
}

sealed class SellerSectionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerSectionsItemRecentViewHolder(
        val binding: SellerSectionsItemRecentBinding
    ) : SellerSectionsViewHolder(binding.root)

    class SellerSectionsItemUpcomingViewHolder(
        val binding: SellerSectionsItemUpcomingBinding
    ) : SellerSectionsViewHolder(binding.root)

    class SellerSectionsSubtitleViewHolder(
        val binding: SubtitleLargeItemBinding
    ) : SellerSectionsViewHolder(binding.root)

    class SellerSectionEmptyViewHolder(
        val binding: EmptyListItemBinding
    ) : SellerSectionsViewHolder(binding.root)
}

sealed class SellerSectionsListModel {
    data class SellerSectionsItemModel(
        val sellerSectionBasic: SellerSectionBasic
    ) : SellerSectionsListModel()

    data class SellerSectionsSubtitleModel(
        val subtitle: String
    ) : SellerSectionsListModel()

    object SellerSectionsEmptyModel : SellerSectionsListModel()
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
            oldItem is SellerSectionsEmptyModel && newItem is SellerSectionsEmptyModel ->
                true
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
            oldItem is SellerSectionsEmptyModel && newItem is SellerSectionsEmptyModel ->
                true
            else -> false
        }
    }
}