package com.foobarust.android.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.databinding.DeliveryOptionItemBinding

/**
 * Created by kevin on 1/10/21
 */

class DeliveryOptionsAdapter : ListAdapter<DeliveryOptionsItemModel, DeliveryOptionsItemViewHolder>(
    DeliveryOptionsItemModelDiff
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryOptionsItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DeliveryOptionsItemViewHolder(
            DeliveryOptionItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DeliveryOptionsItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface DeliveryOptionsAdapterListener {
        fun onDeliveryOptionSelected(deliveryOptionId: String)
    }
}

data class DeliveryOptionsItemModel(
    val optionId: String,
    val title: String,
    @DrawableRes val drawable: Int,
    val isSelected: Boolean = false
)

class DeliveryOptionsItemViewHolder(
    val binding: DeliveryOptionItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(optionItemModel: DeliveryOptionsItemModel) = binding.run {
        this.optionItemModel = optionItemModel
        executePendingBindings()
    }
}

object DeliveryOptionsItemModelDiff : DiffUtil.ItemCallback<DeliveryOptionsItemModel>() {
    override fun areItemsTheSame(
        oldItem: DeliveryOptionsItemModel,
        newItem: DeliveryOptionsItemModel
    ): Boolean {
        return oldItem.optionId == newItem.optionId
    }

    override fun areContentsTheSame(
        oldItem: DeliveryOptionsItemModel,
        newItem: DeliveryOptionsItemModel
    ): Boolean {
        return oldItem == newItem
    }
}