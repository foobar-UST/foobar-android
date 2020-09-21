package com.foobarust.android.seller

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.foobarust.android.R
import com.foobarust.android.seller.PromotionBannerAdapter.PromotionBannerAdapterListener
import com.foobarust.android.utils.bindGlideSrc
import com.google.android.material.card.MaterialCardView
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class PromotionBannerAdapter(
    private val listenerBanner: PromotionBannerAdapterListener
) : BaseBannerAdapter<PromotionItem, PromotionBannerViewHolder>() {

    override fun createViewHolder(itemView: View, viewType: Int): PromotionBannerViewHolder {
        return PromotionBannerViewHolder(itemView, listenerBanner)
    }

    override fun onBind(
        holderBanner: PromotionBannerViewHolder?,
        data: PromotionItem?,
        position: Int,
        pageSize: Int
    ) {
        holderBanner?.bindData(data, position, pageSize)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_promotion_banner
    }

    interface PromotionBannerAdapterListener {
        fun onPromotionBannerItemClicked(promotionItem: PromotionItem)
    }
}

class PromotionBannerViewHolder(
    itemView: View,
    private val listenerBanner: PromotionBannerAdapterListener
) : BaseViewHolder<PromotionItem>(itemView) {

    override fun bindData(data: PromotionItem?, position: Int, pageSize: Int) {
        val promotionCard = findView<MaterialCardView>(R.id.promotion_card_view)
        val promotionImage = findView<ImageView>(R.id.promotion_image_view)

        // Promotion card
        promotionCard.setOnClickListener {
            listenerBanner.onPromotionBannerItemClicked(data!!)
        }

        // Load the user image
        promotionImage.bindGlideSrc(
            drawableRes = data?.drawable,
            centerCrop = true
        )
    }
}

data class PromotionItem(
    @DrawableRes val drawable: Int
)



