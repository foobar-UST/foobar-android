package com.foobarust.android.selleritem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SellerItemsListItemBinding
import com.foobarust.android.selleritem.SellerItemsAdapter.SellerItemsAdapterListener

/**
 * Created by kevin on 10/4/20
 */

class SellerItemsAdapter(
    private val listener: SellerItemsAdapterListener
) : PagingDataAdapter<SellerItemsListModel, SellerItemsViewHolder>(SellerItemsListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerItemsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SellerItemsViewHolder(
            SellerItemsListItemBinding.inflate(inflater, parent, false),
            listener
        )
    }

    override fun onBindViewHolder(holder: SellerItemsViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    interface SellerItemsAdapterListener {
        fun onSellerItemClicked(itemId: String)
    }
}

class SellerItemsViewHolder(
    val binding: SellerItemsListItemBinding,
    val listener: SellerItemsAdapterListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(itemModel: SellerItemsListModel) = binding.run {
        with(root) {
            isEnabled = itemModel.itemPurchasable
            setOnClickListener {
                listener.onSellerItemClicked(itemModel.itemId)
            }
        }

        with(titleTextView) {
            isEnabled = itemModel.itemPurchasable
            text = itemModel.itemTitle
        }

        with(priceTextView) {
            isEnabled = itemModel.itemPurchasable
            text = context.getString(R.string.seller_item_data_format_price, itemModel.itemPrice)
        }
    }
}

data class SellerItemsListModel(
    val itemId: String,
    val itemTitle: String,
    val itemPrice: Double,
    val itemPurchasable: Boolean
)

object SellerItemsListModelDiff : DiffUtil.ItemCallback<SellerItemsListModel>() {
    override fun areItemsTheSame(
        oldItem: SellerItemsListModel,
        newItem: SellerItemsListModel
    ): Boolean {
        return oldItem.itemId == newItem.itemId
    }

    override fun areContentsTheSame(
        oldItem: SellerItemsListModel,
        newItem: SellerItemsListModel
    ): Boolean {
        return oldItem == newItem
    }
}