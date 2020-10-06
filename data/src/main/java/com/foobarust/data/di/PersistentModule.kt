package com.foobarust.data.di

import com.foobarust.data.BuildConfig
import com.foobarust.data.repositories.PreferencesRepositoryImpl
import com.foobarust.domain.repositories.PreferencesRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * Created by kevin on 9/28/20
 */

@Module
@InstallIn(ApplicationComponent::class)
abstract class PersistentModule {

    companion object {
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
        fun provideStorage(): FirebaseStorage {
            return Firebase.storage
        }

        @Singleton
        @Provides
        fun provideStorageReference(storage: FirebaseStorage): StorageReference {
            return storage.reference
        }
    }

    @Singleton
    @Binds
    abstract fun providePreferencesRepository(
        preferencesRepositoryImpl: PreferencesRepositoryImpl
    ): PreferencesRepository
}