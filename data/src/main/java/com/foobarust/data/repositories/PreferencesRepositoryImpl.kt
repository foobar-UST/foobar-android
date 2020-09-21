package com.foobarust.data.repositories

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import com.foobarust.domain.repositories.PreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by kevin on 8/28/20
 */

private const val PREFS_NAME = "foobarust"
private const val PREF_KEY_EMAIL_VERIFY = "pref_email_to_be_verified"
private const val PREF_KEY_ONBOARDING_COMPLETED = "pref_onboarding_completed"

class PreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferencesRepository {

    private val prefs: Lazy<SharedPreferences> = lazy {
        context.applicationContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    override var emailToBeVerified by StringPreference(prefs, PREF_KEY_EMAIL_VERIFY, null)

    override var isOnboardingCompleted by BooleanPreference(prefs, PREF_KEY_ONBOARDING_COMPLETED, false)
}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val key: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit { putBoolean(key, value) }
    }
}

class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val key: String,
    private val defaultValue: String?
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.value.getString(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit { putString(key, value) }
    }
}

