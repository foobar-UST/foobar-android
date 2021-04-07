package com.foobarust.android.promotion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foobarust.android.R
import com.foobarust.android.databinding.AdvertiseItemBinding
import com.foobarust.android.promotion.AdvertiseAdapter.AdvertiseAdapterListener
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class AdvertiseAdapter(
    private val listener: AdvertiseAdapterListener
) : BaseBannerAdapter<AdvertiseItemModel, AdvertiseItemViewHolder>() {

    override fun createViewHolder(
        parent: ViewGroup,
        itemView: View?,
        viewType: Int
    ): AdvertiseItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return AdvertiseItemViewHolder(
            AdvertiseItemBinding.inflate(inflater, parent, false),
            listener
        )
    }

    override fun onBind(
        holder: AdvertiseItemViewHolder?,
        data: AdvertiseItemModel?,
        position: Int,
        pageSize: Int
    ) {
        holder?.bindData(data, position, pageSize)
    }

    override fun getLayoutId(viewType: Int): Int = R.layout.advertise_item

    interface AdvertiseAdapterListener {
        fun onAdvertiseItemClicked(advertiseBasic: AdvertiseBasic)
    }
}

class AdvertiseItemViewHolder(
    private val binding: AdvertiseItemBinding,
    private val listener: AdvertiseAdapterListener
) : BaseViewHolder<AdvertiseItemModel>(binding.root) {

    override fun bindData(data: AdvertiseItemModel?, position: Int, pageSize: Int) = binding.run {
        val advertiseBasic = data?.advertiseBasic ?: return@run

        promotionImageView.loadGlideUrl(
            imageUrl = advertiseBasic.imageUrl,
            centerCrop = true,
            placeholder = R.drawable.placeholder_card
        )

        promotionCardView.setOnClickListener {
            listener.onAdvertiseItemClicked(advertiseBasic)
        }
    }
}

data class AdvertiseItemModel(val advertiseBasic: AdvertiseBasic)



