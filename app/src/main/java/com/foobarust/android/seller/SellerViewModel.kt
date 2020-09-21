package com.foobarust.android.seller

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.foobarust.android.R

class SellerViewModel @ViewModelInject constructor() : ViewModel() {

    val promotionItems = liveData {
        emit(listOf(
            PromotionItem(drawable = R.drawable.sample_image_1),
            PromotionItem(drawable = R.drawable.sample_image_2),
            PromotionItem(drawable = R.drawable.sample_image_3),
            PromotionItem(drawable = R.drawable.sample_image_4),
            PromotionItem(drawable = R.drawable.sample_image_5)
        ))
    }
}