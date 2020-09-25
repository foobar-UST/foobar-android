package com.foobarust.data.utils

/**
 * Created by kevin on 9/23/20
 */

@Suppress("SimpleRedundantLet")
fun Any?.toStringOrNull(): String? {
    return this?.let { it.toString() }
}