package com.foobarust.android.sellerdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SellerItemDetailAmountItemBinding
import com.foobarust.android.databinding.SellerItemDetailNotesItemBinding
import com.foobarust.android.databinding.SellerItemDetailSubtitleItemBinding
import com.foobarust.android.sellerdetail.SellerItemDetailListModel.*
import com.foobarust.android.sellerdetail.SellerItemDetailViewHolder.*

/**
 * Created by kevin on 10/12/20
 */

class SellerItemDetailAdapter(
    private val listener: SellerItemDetailAdapterListener
) : ListAdapter<SellerItemDetailListModel, SellerItemDetailViewHolder>(SellerItemDetailListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerItemDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.seller_item_detail_subtitle_item -> SellerItemDetailSubtitleViewHolder(
                SellerItemDetailSubtitleItemBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_item_detail_notes_item -> SellerItemDetailNotesViewHolder(
                SellerItemDetailNotesItemBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_item_detail_amount_item -> SellerItemDetailAmountViewHolder(
                SellerItemDetailAmountItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerItemDetailViewHolder, position: Int) {
        when (holder) {
            is SellerItemDetailSubtitleViewHolder -> holder.binding.run {
                subtitleModel = getItem(position) as SellerItemDetailSubtitleModel
                executePendingBindings()
            }

            is SellerItemDetailNotesViewHolder -> holder.binding.run {
                notesEditText.doOnTextChanged { text, _, _, _ ->
                    listener.onNotesChanged(text.toString())
                }
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SellerItemDetailSubtitleModel -> R.layout.seller_item_detail_subtitle_item
            is SellerItemDetailNotesModel -> R.layout.seller_item_detail_notes_item
            is SellerItemDetailAmountModel -> R.layout.seller_item_detail_amount_item
        }
    }

    interface SellerItemDetailAdapterListener {
        fun onNotesChanged(notes: String)
        fun onAmountIncremented()
        fun onAmountDecremented()
    }
}

sealed class SellerItemDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerItemDetailSubtitleViewHolder(
        val binding: SellerItemDetailSubtitleItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailNotesViewHolder(
        val binding: SellerItemDetailNotesItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailAmountViewHolder(
        val binding: SellerItemDetailAmountItemBinding
    ) : SellerItemDetailViewHolder(binding.root)
}

sealed class SellerItemDetailListModel {
    data class SellerItemDetailSubtitleModel(
        val subtitle: String
    ) : SellerItemDetailListModel()

    data class SellerItemDetailAmountModel(
        val amount: Int = 1
    ) : SellerItemDetailListModel()

    object SellerItemDetailNotesModel : SellerItemDetailListModel()
}

object SellerItemDetailListModelDiff : DiffUtil.ItemCallback<SellerItemDetailListModel>() {
    override fun areItemsTheSame(oldItem: SellerItemDetailListModel, newItem: SellerItemDetailListModel): Boolean {
       return when {
           oldItem is SellerItemDetailSubtitleModel && newItem is SellerItemDetailSubtitleModel ->
               oldItem.subtitle == newItem.subtitle
           oldItem is SellerItemDetailAmountModel && newItem is SellerItemDetailAmountModel -> true
           oldItem is SellerItemDetailNotesModel && newItem is SellerItemDetailNotesModel -> true
           else -> false
       }
    }

    override fun areContentsTheSame(oldItem: SellerItemDetailListModel, newItem: SellerItemDetailListModel): Boolean {
        return when {
            oldItem is SellerItemDetailSubtitleModel && newItem is SellerItemDetailSubtitleModel ->
                oldItem.subtitle == newItem.subtitle
            oldItem is SellerItemDetailAmountModel && newItem is SellerItemDetailAmountModel ->
                oldItem.amount == newItem.amount
            oldItem is SellerItemDetailNotesModel && newItem is SellerItemDetailNotesModel -> true
            else -> false
        }
    }
}