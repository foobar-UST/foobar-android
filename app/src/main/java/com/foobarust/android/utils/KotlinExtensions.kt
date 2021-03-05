package com.foobarust.android.utils

import kotlin.math.round

/**
 * Created by kevin on 2/25/21
 */

fun<T> List<T>.previousOf(item: T): T? {
    val previousIndex = if (indexOf(item) > -1) indexOf(item) - 1 else return null
    return getOrNull(previousIndex)
}

fun<T> List<T>.nextOf(item: T): T? {
    val nextIndex = if (indexOf(item) > -1) indexOf(item) + 1 else return null
    return getOrNull(nextIndex)
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}