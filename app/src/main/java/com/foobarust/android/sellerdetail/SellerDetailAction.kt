package com.foobarust.android.sellerdetail

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.foobarust.android.R

/**
 * Created by kevin on 10/17/20
 */

data class SellerDetailAction(
    val id: String,
    val title: String,
    @DrawableRes val drawableRes: Int? = null,
    @ColorRes val colorRes: Int = R.color.material_on_background_emphasis_medium
)