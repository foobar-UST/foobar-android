package com.foobarust.android.sellerdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.databinding.SellerItemsBasicItemBinding
import com.foobarust.android.sellerdetail.SellerItemsAdapter.SellerItemsAdapterListener
import com.foobarust.domain.models.seller.SellerItemBasic

/**
 * Created by kevin on 10/4/20
 */

class SellerItemsAdapter(
    private val listener: SellerItemsAdapterListener
) : PagingDataAdapter<SellerItemBasic, SellerItemsViewHolder>(SellerItemsDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerItemsViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return SellerItemsViewHolder(
            SellerItemsBasicItemBinding.inflate(inflater, parent, false),
            listener
        )
    }

    override fun onBindViewHolder(holder: SellerItemsViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    interface SellerItemsAdapterListener {
        fun onSellerItemClicked(sellerItemBasic: SellerItemBasic)
    }
}

class SellerItemsViewHolder(
    val binding: SellerItemsBasicItemBinding,
    val listener: SellerItemsAdapterListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(sellerItemBasic: SellerItemBasic) = binding.run {
        this.sellerItemBasic = sellerItemBasic
        listener = this@SellerItemsViewHolder.listener
        executePendingBindings()
    }
}

object SellerItemsDiff : DiffUtil.ItemCallback<SellerItemBasic>() {
    override fun areItemsTheSame(oldItem: SellerItemBasic, newItem: SellerItemBasic): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SellerItemBasic, newItem: SellerItemBasic): Boolean {
        return oldItem == newItem
    }
}