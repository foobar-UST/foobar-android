package com.foobarust.data.di

import com.foobarust.data.mappers.AuthMapper
import com.foobarust.data.repositories.AuthRepositoryImpl
import com.foobarust.domain.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kevin on 9/14/20
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Singleton
    @Binds
    abstract fun bindsAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    companion object {
        @Singleton
        @Provides
        fun provideFirebaseAuth(): FirebaseAuth {
            return Firebase.auth
        }

        @Singleton
        @Provides
        fun provideAuthMapper(): AuthMapper = AuthMapper()
    }
}