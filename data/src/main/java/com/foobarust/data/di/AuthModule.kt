package com.foobarust.data.di

import com.foobarust.data.repositories.FirebaseAuthRepository
import com.foobarust.domain.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * Created by kevin on 8/27/20
 */

@Module
@InstallIn(ApplicationComponent::class)
abstract class AuthModule {

    companion object {
        @Singleton
        @Provides
        fun provideFirebaseAuth(): FirebaseAuth {
            return Firebase.auth
        }
    }

    @Singleton
    @Binds
    abstract fun bindsAuthRepository(
        firebaseAuthRepository: FirebaseAuthRepository
    ): AuthRepository
}
