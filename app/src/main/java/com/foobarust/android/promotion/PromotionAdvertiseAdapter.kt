package com.foobarust.android.promotion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foobarust.android.R
import com.foobarust.android.databinding.PromotionAdvertiseItemBinding
import com.foobarust.android.promotion.PromotionAdvertiseAdapter.PromotionAdvertiseAdapterListener
import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class PromotionAdvertiseAdapter(
    private val listener: PromotionAdvertiseAdapterListener
) : BaseBannerAdapter<PromotionAdvertiseItemModel, PromotionAdvertiseItemViewHolder>() {

    override fun createViewHolder(
        parent: ViewGroup,
        itemView: View?,
        viewType: Int
    ): PromotionAdvertiseItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return PromotionAdvertiseItemViewHolder(
            PromotionAdvertiseItemBinding.inflate(inflater, parent, false),
            listener
        )
    }

    override fun onBind(
        holder: PromotionAdvertiseItemViewHolder?,
        data: PromotionAdvertiseItemModel?,
        position: Int,
        pageSize: Int
    ) {
        holder?.bindData(data, position, pageSize)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.promotion_advertise_item
    }

    interface PromotionAdvertiseAdapterListener {
        fun onPromotionAdvertiseItemClicked(advertiseBasic: AdvertiseBasic)
    }
}

class PromotionAdvertiseItemViewHolder(
    private val binding: PromotionAdvertiseItemBinding,
    private val listener: PromotionAdvertiseAdapterListener
) : BaseViewHolder<PromotionAdvertiseItemModel>(binding.root) {

    override fun bindData(data: PromotionAdvertiseItemModel?, position: Int, pageSize: Int) {
        binding.run {
            advertiseItemModel = data
            listener = this@PromotionAdvertiseItemViewHolder.listener
            executePendingBindings()
        }
    }
}

data class PromotionAdvertiseItemModel(val advertiseBasic: AdvertiseBasic)



