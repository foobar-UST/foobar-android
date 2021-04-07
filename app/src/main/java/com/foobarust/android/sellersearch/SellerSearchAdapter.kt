package com.foobarust.android.sellersearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SellerSearchItemBinding
import com.foobarust.android.sellersearch.SellerSearchListModel.SellerSearchItemModel
import com.foobarust.android.sellersearch.SellerSearchViewHolder.SellerSearchItemViewHolder
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.android.utils.themeColor
import com.foobarust.domain.models.seller.SellerType

/**
 * Created by kevin on 2/23/21
 */

class SellerSearchAdapter(
    private val listener: SellerSearchAdapterListener
) : ListAdapter<SellerSearchListModel, SellerSearchViewHolder>(SellerSearchListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerSearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.seller_search_item -> SellerSearchItemViewHolder(
                SellerSearchItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerSearchViewHolder, position: Int) {
        when (holder) {
            is SellerSearchItemViewHolder -> bindSearchItem(
                binding = holder.binding,
                searchItemModel = getItem(position) as SellerSearchItemModel
            )
        }
    }

    private fun bindSearchItem(
        binding: SellerSearchItemBinding,
        searchItemModel: SellerSearchItemModel
    ) = binding.run {
        root.setOnClickListener {
            listener.onSellerItemClicked(searchItemModel.sellerId, searchItemModel.sellerType)
        }

        sellerItemImageView.loadGlideUrl(
            imageUrl = searchItemModel.sellerImageUrl,
            centerCrop = true,
            placeholder = R.drawable.placeholder_card
        )

        sellerNameTextView.text = searchItemModel.sellerName

        sellerTagsTextView.text = searchItemModel.sellerTags

        with(sellerTypeTextView) {
            if (searchItemModel.sellerType == SellerType.ON_CAMPUS) {
                text = context.getString(R.string.seller_type_on_campus)
                setTextColor(context.themeColor(R.attr.colorSecondary))
            } else {
                text = context.getString(R.string.seller_type_off_campus)
                setTextColor(context.themeColor(R.attr.colorPrimary))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SellerSearchItemModel -> R.layout.seller_search_item
        }
    }

    interface SellerSearchAdapterListener {
        fun onSellerItemClicked(sellerId: String, sellerType: SellerType)
    }
}

sealed class SellerSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerSearchItemViewHolder(
        val binding: SellerSearchItemBinding
    ) : SellerSearchViewHolder(binding.root)
}

sealed class SellerSearchListModel {
    data class SellerSearchItemModel(
        val sellerId: String,
        val sellerName: String,
        val sellerType: SellerType,
        val sellerTags: String,
        val sellerImageUrl: String?
    ) : SellerSearchListModel()
}

object SellerSearchListModelDiff : DiffUtil.ItemCallback<SellerSearchListModel>() {
    override fun areItemsTheSame(
        oldItem: SellerSearchListModel,
        newItem: SellerSearchListModel
    ): Boolean {
        return when {
            oldItem is SellerSearchItemModel && newItem is SellerSearchItemModel ->
                oldItem.sellerId == newItem.sellerId
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SellerSearchListModel,
        newItem: SellerSearchListModel
    ): Boolean {
        return when {
            oldItem is SellerSearchItemModel && newItem is SellerSearchItemModel ->
                oldItem == newItem
            else -> false
        }
    }
}

