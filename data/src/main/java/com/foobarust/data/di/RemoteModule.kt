package com.foobarust.data.di

import com.foobarust.data.repositories.UserRepositoryImpl
import com.foobarust.domain.repositories.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
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
 * Created by kevin on 9/14/20
 */

@Module
@InstallIn(ApplicationComponent::class)
abstract class RemoteModule {

    companion object {
        @Singleton
        @Provides
        fun provideFirestore(): FirebaseFirestore {
            /*
            val settings = firestoreSettings {
                isPersistenceEnabled = false
            }

             */
            return Firebase.firestore
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
    abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}