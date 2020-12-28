package com.foobarust.android.promotion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.PromotionAdvertiseSectionBinding
import com.foobarust.android.databinding.PromotionSuggestSectionBinding
import com.foobarust.android.databinding.SubtitleItemBinding
import com.foobarust.android.promotion.PromotionAdvertiseAdapter.PromotionAdvertiseAdapterListener
import com.foobarust.android.promotion.PromotionListModel.*
import com.foobarust.android.promotion.PromotionSuggestAdapter.PromotionSuggestAdapterListener
import com.foobarust.android.promotion.PromotionViewHolder.*
import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.foobarust.domain.models.promotion.SuggestBasic

/**
 * Created by kevin on 9/29/20
 */

class PromotionAdapter(
    private val lifecycle: Lifecycle,
    private val advertiseAdapterListener: PromotionAdvertiseAdapterListener,
    private val suggestAdapterListener: PromotionSuggestAdapterListener
) : ListAdapter<PromotionListModel, PromotionViewHolder>(PromotionListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.promotion_advertise_section -> PromotionAdvertiseViewHolder(
                PromotionAdvertiseSectionBinding.inflate(inflater, parent, false)
            )

            R.layout.promotion_suggest_section -> PromotionSuggestViewHolder(
                PromotionSuggestSectionBinding.inflate(inflater, parent, false)
            )

            R.layout.subtitle_item -> PromotionSubtitleViewHolder(
                SubtitleItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        when (holder) {
            is PromotionAdvertiseViewHolder -> holder.binding.run {
                val advertiseAdapter = PromotionAdvertiseAdapter(advertiseAdapterListener)
                val advertiseItemModels = (getItem(position) as PromotionAdvertiseModel).advertiseBasics
                    .map { PromotionAdvertiseItemModel(advertiseBasic = it) }

                viewPager.apply {
                    setAdapter(advertiseAdapter)
                    setLifecycleRegistry(lifecycle)

                    // Banner item margin
                    val margin = resources.getDimensionPixelOffset(R.dimen.spacing_xmedium)
                    setPageMargin(margin)
                    setRevealWidth(margin, margin)

                    // Indicator
                    setIndicatorView(scrollIndicator)

                    removeDefaultPageTransformer()
                }.create(advertiseItemModels)

                executePendingBindings()
            }

            is PromotionSuggestViewHolder -> holder.binding.run {
                val suggestItems = (getItem(position) as PromotionSuggestModel).suggestBasics
                val suggestAdapter = PromotionSuggestAdapter(suggestAdapterListener)

                suggestRecyclerView.run {
                    adapter = suggestAdapter
                    setHasFixedSize(true)
                }

                suggestAdapter.submitList(suggestItems)
                executePendingBindings()
            }

            is PromotionSubtitleViewHolder -> holder.binding.run {
                subtitle = (getItem(position) as PromotionSubtitleModel).subtitle
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PromotionAdvertiseModel -> R.layout.promotion_advertise_section
            is PromotionSuggestModel -> R.layout.promotion_suggest_section
            is PromotionSubtitleModel -> R.layout.subtitle_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }
}

sealed class PromotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class PromotionAdvertiseViewHolder(
        val binding: PromotionAdvertiseSectionBinding
    ) : PromotionViewHolder(binding.root)

    class PromotionSuggestViewHolder(
        val binding: PromotionSuggestSectionBinding
    ) : PromotionViewHolder(binding.root)

    class PromotionSubtitleViewHolder(
        val binding: SubtitleItemBinding
    ) : PromotionViewHolder(binding.root)
}

sealed class PromotionListModel {
    data class PromotionAdvertiseModel(
        val advertiseBasics: List<AdvertiseBasic>
    ) : PromotionListModel()

    data class PromotionSuggestModel(
        val suggestBasics: List<SuggestBasic>
    ) : PromotionListModel()

    data class PromotionSubtitleModel(
        val subtitle: String
    ) : PromotionListModel()
}

object PromotionListModelDiff : DiffUtil.ItemCallback<PromotionListModel>() {
    override fun areItemsTheSame(
        oldItem: PromotionListModel,
        newItem: PromotionListModel
    ): Boolean {
        return when {
            oldItem is PromotionAdvertiseModel && newItem is PromotionAdvertiseModel -> true  // Single row of banner
            oldItem is PromotionSuggestModel && newItem is PromotionSuggestModel -> true
            oldItem is PromotionSubtitleModel && newItem is PromotionSubtitleModel ->
                oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: PromotionListModel,
        newItem: PromotionListModel
    ): Boolean {
        return when {
            oldItem is PromotionAdvertiseModel && newItem is PromotionAdvertiseModel ->
                oldItem.advertiseBasics == newItem.advertiseBasics
            oldItem is PromotionSuggestModel && newItem is PromotionSuggestModel ->
                oldItem.suggestBasics == newItem.suggestBasics
            oldItem is PromotionSubtitleModel && newItem is PromotionSubtitleModel ->
                oldItem.subtitle == newItem.subtitle
            else -> false
        }
    }
}