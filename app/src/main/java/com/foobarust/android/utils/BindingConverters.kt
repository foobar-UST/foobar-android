package com.foobarust.android.utils

/**
 * Created by kevin on 10/31/20
 */

object BindingConverters {

    @JvmStatic
    fun doubleToFloat(value: Double): Float = value.toFloat()

    @JvmStatic
    fun intToString(value: Int): String = value.toString()
}