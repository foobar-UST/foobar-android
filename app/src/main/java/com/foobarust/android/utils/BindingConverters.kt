package com.foobarust.android.utils

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

/**
 * Created by kevin on 10/31/20
 */

object BindingConverters {

    @JvmStatic
    fun doubleToFloat(value: Double): Float = value.toFloat()

    @JvmStatic
    fun intToString(value: Int): String = value.toString()

    @JvmStatic
    fun colorToDrawable(@ColorInt color: Int): Drawable = ColorDrawable(color)
}