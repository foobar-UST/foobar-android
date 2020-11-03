package com.foobarust.android.promotion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.databinding.PromotionSuggestItemBinding
import com.foobarust.android.promotion.PromotionSuggestAdapter.PromotionSuggestAdapterListener
import com.foobarust.domain.models.SuggestBasic

/**
 * Created by kevin on 9/29/20
 */

class PromotionSuggestAdapter(
    private val listener: PromotionSuggestAdapterListener
) : ListAdapter<SuggestBasic, PromotionSuggestCardViewHolder>(PromotionSuggestItemDiff) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PromotionSuggestCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return PromotionSuggestCardViewHolder(
            PromotionSuggestItemBinding.inflate(inflater, parent, false),
            listener
        )
    }

    override fun onBindViewHolder(holder: PromotionSuggestCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface PromotionSuggestAdapterListener {
        fun onPromotionSuggestItemClicked(suggestBasic: SuggestBasic)
    }
}

class PromotionSuggestCardViewHolder(
    val binding: PromotionSuggestItemBinding,
    val listener: PromotionSuggestAdapterListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(suggestBasic: SuggestBasic) = binding.run {
        this.suggestBasic = suggestBasic
        listener = this@PromotionSuggestCardViewHolder.listener
        executePendingBindings()
    }
}

object PromotionSuggestItemDiff : DiffUtil.ItemCallback<SuggestBasic>() {
    override fun areItemsTheSame(
        oldItem: SuggestBasic,
        newItem: SuggestBasic
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: SuggestBasic,
        newItem: SuggestBasic
    ): Boolean {
        return oldItem == newItem
    }
}
