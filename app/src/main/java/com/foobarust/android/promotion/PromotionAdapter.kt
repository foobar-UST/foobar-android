package com.foobarust.android.promotion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.PromotionAdvertiseSectionBinding
import com.foobarust.android.promotion.PromotionListModel.PromotionAdvertiseModel
import com.foobarust.android.promotion.PromotionViewHolder.PromotionAdvertiseViewHolder
import com.foobarust.domain.models.promotion.AdvertiseBasic

/**
 * Created by kevin on 9/29/20
 */

class PromotionAdapter(
    private val fragment: Fragment
) : ListAdapter<PromotionListModel, PromotionViewHolder>(PromotionListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.promotion_advertise_section -> PromotionAdvertiseViewHolder(
                PromotionAdvertiseSectionBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        when (holder) {
            is PromotionAdvertiseViewHolder -> bindAdvertiseSection(
                binding = holder.binding,
                advertiseModel = getItem(position) as PromotionAdvertiseModel
            )
        }
    }

    private fun bindAdvertiseSection(
        binding: PromotionAdvertiseSectionBinding,
        advertiseModel: PromotionAdvertiseModel
    ) = binding.run {
        val advertiseAdapter = AdvertiseAdapter(
            fragment as AdvertiseAdapter.AdvertiseAdapterListener
        )
        val advertiseItemModels = advertiseModel.advertiseBasics.map {
            AdvertiseItemModel(advertiseBasic = it)
        }

        // Setup banner view pager
        viewPager.apply {
            setAdapter(advertiseAdapter)
            setLifecycleRegistry(fragment.lifecycle)

            // Banner item margin
            val margin = resources.getDimensionPixelOffset(R.dimen.spacing_xmedium)
            setPageMargin(margin)
            setRevealWidth(margin, margin)

            setIndicatorView(scrollIndicator)
            removeDefaultPageTransformer()
        }.create(advertiseItemModels)

        // Hide indicator view when there is only one item
        scrollIndicator.isVisible = advertiseModel.advertiseBasics.size > 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PromotionAdvertiseModel -> R.layout.promotion_advertise_section
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }
}

sealed class PromotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class PromotionAdvertiseViewHolder(
        val binding: PromotionAdvertiseSectionBinding
    ) : PromotionViewHolder(binding.root)
}

sealed class PromotionListModel {
    data class PromotionAdvertiseModel(
        val advertiseBasics: List<AdvertiseBasic>
    ) : PromotionListModel()
}

object PromotionListModelDiff : DiffUtil.ItemCallback<PromotionListModel>() {
    override fun areItemsTheSame(
        oldItem: PromotionListModel,
        newItem: PromotionListModel
    ): Boolean {
        return when {
            oldItem is PromotionAdvertiseModel && newItem is PromotionAdvertiseModel -> true
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
            else -> false
        }
    }
}