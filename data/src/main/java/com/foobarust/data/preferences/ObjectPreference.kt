package com.foobarust.data.preferences

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by kevin on 11/1/20
 */

class ObjectPreference<T>(
    private val gson: Gson,
    private val preferences: SharedPreferences,
    private val key: String
) : ReadWriteProperty<Any, T?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        val jsonString = preferences.getString(key, null)

        return if (jsonString == null)
            null
        else
            gson.fromJson(jsonString, object : TypeToken<T>() {}.type)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        val jsonString = gson.toJson(value)
        preferences.edit { putString(key, jsonString) }
    }
}