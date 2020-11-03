package com.foobarust.data.preferences

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by kevin on 11/1/20
 */

class StringPreference(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defaultValue: String?
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.getString(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.edit { putString(key, value) }
    }
}
