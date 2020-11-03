package com.foobarust.data.di

import android.content.Context
import android.content.SharedPreferences
import com.foobarust.data.BuildConfig
import com.foobarust.data.common.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

/**
 * Created by kevin on 9/28/20
 */

@Module
@InstallIn(ApplicationComponent::class)
object PersistentModule {

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        val settings = firestoreSettings {
            isPersistenceEnabled = false

            if (BuildConfig.USE_FIREBASE_EMULATOR) {
                host = BuildConfig.FIREBASE_EMULATOR_HOST
                isSslEnabled = false
            }
        }

        return Firebase.firestore.apply {
            firestoreSettings = settings
        }
    }

    @Singleton
    @Provides
    fun provideStorage(): FirebaseStorage = Firebase.storage

    @Singleton
    @Provides
    fun provideStorageReference(storage: FirebaseStorage): StorageReference = storage.reference

    @Singleton
    @Provides
    fun provideGson(): Gson = Gson()

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    }
}