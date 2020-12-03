package com.foobarust.android.sellermisc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SellerMiscAddressItemBinding
import com.foobarust.android.databinding.SellerMiscContactItemBinding
import com.foobarust.android.databinding.SellerMiscDescriptionItemBinding
import com.foobarust.android.databinding.SellerMiscOpeningHoursItemBinding
import com.foobarust.android.sellermisc.SellerMiscListModel.*
import com.foobarust.android.sellermisc.SellerMiscViewHolder.*

/**
 * Created by kevin on 11/5/20
 */

class SellerMiscAdapter : ListAdapter<SellerMiscListModel, SellerMiscViewHolder>(SellerMiscListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerMiscViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.seller_misc_address_item -> SellerMiscAddressViewHolder(
                SellerMiscAddressItemBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_misc_contact_item -> SellerMiscContactViewHolder(
                SellerMiscContactItemBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_misc_opening_hours_item -> SellerMiscOpeningHoursViewHolder(
                SellerMiscOpeningHoursItemBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_misc_description_item -> SellerMiscDescriptionViewHolder(
                SellerMiscDescriptionItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerMiscViewHolder, position: Int) {
        when (holder) {
            is SellerMiscAddressViewHolder -> holder.binding.run {
                addressModel = getItem(position) as SellerMiscAddressModel
                executePendingBindings()
            }

            is SellerMiscContactViewHolder -> holder.binding.run {
                contactModel = getItem(position) as SellerMiscContactModel
                executePendingBindings()
            }

            is SellerMiscOpeningHoursViewHolder -> holder.binding.run {
                openingHoursModel = getItem(position) as SellerMiscOpeningHoursModel
                executePendingBindings()
            }

            is SellerMiscDescriptionViewHolder -> holder.binding.run {
                descriptionModel = getItem(position) as SellerMiscDescriptionModel
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SellerMiscAddressModel -> R.layout.seller_misc_address_item
            is SellerMiscContactModel -> R.layout.seller_misc_contact_item
            is SellerMiscOpeningHoursModel -> R.layout.seller_misc_opening_hours_item
            is SellerMiscDescriptionModel -> R.layout.seller_misc_description_item
        }
    }
}

sealed class SellerMiscViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerMiscAddressViewHolder(
        val binding: SellerMiscAddressItemBinding
    ) : SellerMiscViewHolder(binding.root)

    class SellerMiscContactViewHolder(
        val binding: SellerMiscContactItemBinding
    ) : SellerMiscViewHolder(binding.root)

    class SellerMiscOpeningHoursViewHolder(
        val binding: SellerMiscOpeningHoursItemBinding
    ) : SellerMiscViewHolder(binding.root)

    class SellerMiscDescriptionViewHolder(
        val binding: SellerMiscDescriptionItemBinding
    ) : SellerMiscViewHolder(binding.root)
}

sealed class SellerMiscListModel {
    data class SellerMiscAddressModel(
        val name: String,
        val address: String
    ) : SellerMiscListModel()

    data class SellerMiscContactModel(
        val phoneNum: String,
        val website: String?
    ) : SellerMiscListModel()

    data class SellerMiscOpeningHoursModel(
        val openingHours: String
    ) : SellerMiscListModel()

    data class SellerMiscDescriptionModel(
        val description: String?
    ) : SellerMiscListModel()
}

object SellerMiscListModelDiff : DiffUtil.ItemCallback<SellerMiscListModel>() {
    override fun areItemsTheSame(
        oldItem: SellerMiscListModel,
        newItem: SellerMiscListModel
    ): Boolean {
        return when {
            oldItem is SellerMiscAddressModel && newItem is SellerMiscAddressModel -> true
            oldItem is SellerMiscContactModel && newItem is SellerMiscContactModel -> true
            oldItem is SellerMiscOpeningHoursModel && newItem is SellerMiscDescriptionModel -> true
            oldItem is SellerMiscDescriptionModel && newItem is SellerMiscAddressModel -> true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SellerMiscListModel,
        newItem: SellerMiscListModel
    ): Boolean {
        return when {
            oldItem is SellerMiscAddressModel && newItem is SellerMiscAddressModel ->
                oldItem == newItem
            oldItem is SellerMiscContactModel && newItem is SellerMiscContactModel ->
                oldItem == newItem
            oldItem is SellerMiscOpeningHoursModel && newItem is SellerMiscDescriptionModel ->
                oldItem == newItem
            oldItem is SellerMiscDescriptionModel && newItem is SellerMiscAddressModel ->
                oldItem == newItem
            else -> false
        }
    }
}