package com.foobarust.android.promotion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.PromotionAdvertiseSectionBinding
import com.foobarust.android.databinding.PromotionSuggestSectionBinding
import com.foobarust.android.databinding.SellerSubtitleItemBinding
import com.foobarust.android.promotion.PromotionListModel.*
import com.foobarust.android.promotion.PromotionViewHolder.*
import com.foobarust.android.seller.SellerFragment
import com.foobarust.domain.models.AdvertiseBasic
import com.foobarust.domain.models.SuggestBasic

/**
 * Created by kevin on 9/29/20
 */

class PromotionAdapter(
    private val sellerFragment: SellerFragment
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

            R.layout.seller_subtitle_item -> PromotionSubtitleViewHolder(
                SellerSubtitleItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        when (holder) {
            is PromotionAdvertiseViewHolder -> holder.binding.run {
                val promotionAdapter = PromotionAdvertiseAdapter(sellerFragment)
                val promotionItems = (getItem(position) as PromotionAdvertiseModel).advertiseBasics

                viewPager.apply {
                    setAdapter(promotionAdapter)
                    setLifecycleRegistry(sellerFragment.lifecycle)

                    // Banner item margin
                    val margin = resources.getDimensionPixelOffset(R.dimen.spacing_xmedium)
                    setPageMargin(margin)
                    setRevealWidth(margin, margin)

                    // Indicator
                    setIndicatorView(scrollIndicator)

                    removeDefaultPageTransformer()
                }.create(promotionItems)

                executePendingBindings()
            }

            is PromotionSuggestViewHolder -> holder.binding.run {
                val suggestItems = (getItem(position) as PromotionSuggestModel).suggestBasics
                val suggestAdapter = PromotionSuggestAdapter(sellerFragment)

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
            is PromotionSubtitleModel -> R.layout.seller_subtitle_item
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
        val binding: SellerSubtitleItemBinding
    ) : PromotionViewHolder(binding.root)
}

sealed class PromotionListModel {
    data class PromotionAdvertiseModel(
        val advertiseBasics: List<AdvertiseBasic>
    ) : PromotionListModel()

    data class PromotionSuggestModel(
        // TODO: create suggest data object
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