package com.foobarust.android.promotion

import android.view.View
import android.widget.ImageView
import com.foobarust.android.R
import com.foobarust.android.promotion.PromotionAdvertiseAdapter.PromotionAdvertiseAdapterListener
import com.foobarust.android.utils.bindGlideUrl
import com.foobarust.domain.models.promotion.AdvertiseBasic
import com.google.android.material.card.MaterialCardView
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class PromotionAdvertiseAdapter(
    private val listener: PromotionAdvertiseAdapterListener
) : BaseBannerAdapter<AdvertiseBasic, PromotionCardViewHolder>() {

    override fun createViewHolder(itemView: View, viewType: Int): PromotionCardViewHolder {
        return PromotionCardViewHolder(itemView, listener)
    }

    override fun onBind(
        holder: PromotionCardViewHolder?,
        data: AdvertiseBasic?,
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

class PromotionCardViewHolder(
    itemView: View,
    private val listener: PromotionAdvertiseAdapterListener
) : BaseViewHolder<AdvertiseBasic>(itemView) {

    override fun bindData(data: AdvertiseBasic?, position: Int, pageSize: Int) {
        val promotionCard = findView<MaterialCardView>(R.id.promotion_card_view)
        val promotionImage = findView<ImageView>(R.id.promotion_image_view)

        // Promotion card
        promotionCard.setOnClickListener {
            listener.onPromotionAdvertiseItemClicked(data!!)
        }

        // Load the user image
        promotionImage.bindGlideUrl(
            imageUrl = data?.imageUrl,
            placeholder = R.drawable.placeholder_card,
            centerCrop = true
        )
    }
}



