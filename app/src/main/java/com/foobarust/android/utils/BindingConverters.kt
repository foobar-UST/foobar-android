package com.foobarust.android.utils

import androidx.databinding.InverseMethod

/**
 * Created by kevin on 10/31/20
 */

object BindingConverters {

    @JvmStatic
    fun doubleToFloat(value: Double): Float = value.toFloat()

    @InverseMethod("doubleToFloat")
    @JvmStatic
    fun floatToDouble(value: Float): Double = value.toDouble()

    @JvmStatic
    fun intToString(value: Int): String = value.toString()

    @InverseMethod("intToString")
    @JvmStatic
    fun stringToInt(value: String): Int = value.toInt()
}