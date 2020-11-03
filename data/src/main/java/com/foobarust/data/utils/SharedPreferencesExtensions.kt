package com.foobarust.data.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import com.foobarust.domain.states.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by kevin on 11/1/20
 */

fun<T> SharedPreferences.getObject(key: String): T? {
    return getString(key, null)?.let {
        Gson().fromJson(it, object : TypeToken<T>() {}.type)
    }
}

fun<T> SharedPreferences.putObject(key: String, value: T?) {
    edit { putString(key, Gson().toJson(value)) }
}

fun SharedPreferences.getBooleanResource(key: String, default: Boolean): Resource<Boolean> {
    return try {
        Resource.Success(getBoolean(key, default))
    } catch (e: Exception) {
        Resource.Error(e.message)
    }
}

fun SharedPreferences.getStringResource(key: String): Resource<String?> {
    return try {
        Resource.Success(getString(key, null))
    } catch (e: Exception) {
        Resource.Error(e.message)
    }
}

fun SharedPreferences.getIntResource(key: String, default: Int): Resource<Int> {
    return try {
        Resource.Success(getInt(key, default))
    } catch (e: Exception) {
        Resource.Error(e.message)
    }
}

fun<T> SharedPreferences.getObjectResource(key: String): Resource<T?> {
    return try {
        Resource.Success(getObject<T>(key))
    } catch (e: Exception) {
        Resource.Error(e.message)
    }
}

fun SharedPreferences.getBooleanFlow(key: String, default: Boolean): Flow<Resource<Boolean>> = flow {
    emit(Resource.Loading())

    try {
        emit(Resource.Success(getBoolean(key, default)))
    } catch (e: Exception) {
        emit(Resource.Error(e.message))
    }
}

fun SharedPreferences.getStringFlow(key: String): Flow<Resource<String?>> = flow {
    emit(Resource.Loading())

    try {
        emit(Resource.Success(getString(key, null)))
    } catch (e: Exception) {
        emit(Resource.Error(e.message))
    }
}

fun SharedPreferences.getIntFlow(key: String, default: Int): Flow<Resource<Int>> = flow {
    emit(Resource.Loading())

    try {
        emit(Resource.Success(getInt(key, default)))
    } catch (e: Exception) {
        emit(Resource.Error(e.message))
    }
}

fun<T> SharedPreferences.getObjectFlow(key: String): Flow<Resource<T?>> = flow {
    emit(Resource.Loading())

    try {
        emit(Resource.Success(getObject<T>(key)))
    } catch (e: Exception) {
        emit(Resource.Error(e.message))
    }
}