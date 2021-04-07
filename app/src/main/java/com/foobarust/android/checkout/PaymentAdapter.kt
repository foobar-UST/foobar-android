package com.foobarust.android.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.databinding.PaymentMethodItemBinding
import com.foobarust.android.utils.setSrc

/**
 * Created by kevin on 1/9/21
 */

class PaymentAdapter(
    private val listener: PaymentAdapterListener
) : ListAdapter<PaymentMethodItemModel, PaymentAdapter.PaymentMethodItemViewHolder>(PaymentMethodItemModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PaymentMethodItemViewHolder(
            PaymentMethodItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PaymentMethodItemViewHolder, position: Int) {
        holder.bind(itemModel = getItem(position), newPosition = position)
    }

    private fun buildNewSelectedList(newSelectedPosition: Int): List<PaymentMethodItemModel> {
        return currentList.mapIndexed { index, paymentMethodItemModel ->
            PaymentMethodItemModel(
                paymentMethodItem = paymentMethodItemModel.paymentMethodItem,
                isSelected = index == newSelectedPosition
            )
        }
    }

    inner class PaymentMethodItemViewHolder(
        private val binding: PaymentMethodItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(itemModel: PaymentMethodItemModel, newPosition: Int) = binding.run {
            // Save new selected item position and deselect the previous one
            root.setOnClickListener {
                submitList(buildNewSelectedList(newPosition))
                listener.onPaymentMethodClicked(identifier = itemModel.paymentMethodItem.identifier)
            }

            itemRadioButton.isChecked = itemModel.isSelected

            with(itemImageView) {
                contentDescription = itemModel.paymentMethodItem.title
                setSrc(itemModel.paymentMethodItem.drawable)
            }

            itemTitleTextView.text = itemModel.paymentMethodItem.title
            itemDescriptionTextView.text = itemModel.paymentMethodItem.description
        }
    }

    interface PaymentAdapterListener {
        fun onPaymentMethodClicked(identifier: String)
    }
}

object PaymentMethodItemModelDiff : DiffUtil.ItemCallback<PaymentMethodItemModel>() {
    override fun areItemsTheSame(
        oldItem: PaymentMethodItemModel,
        newItem: PaymentMethodItemModel
    ): Boolean {
        return oldItem.paymentMethodItem.identifier == newItem.paymentMethodItem.identifier
    }

    override fun areContentsTheSame(
        oldItem: PaymentMethodItemModel,
        newItem: PaymentMethodItemModel
    ): Boolean {
        return oldItem == newItem
    }
}

data class PaymentMethodItemModel(
    val paymentMethodItem: PaymentMethodItem,
    var isSelected: Boolean = false
)