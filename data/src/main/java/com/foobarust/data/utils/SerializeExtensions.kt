package com.foobarust.data.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by kevin on 9/23/20
 */

/**
 * Convert a data class to a map
 */
internal fun<T> T.serializeToMutableMap(): MutableMap<String, Any> {
    val gson = Gson()
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<MutableMap<String, Any>>() {}.type)
}