package com.foobarust.data.di

import com.foobarust.data.repositories.UserRepositoryImpl
import com.foobarust.domain.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * Created by kevin on 9/27/20
 */

@Module(includes = [
    PersistentModule::class
])
@InstallIn(ApplicationComponent::class)
abstract class UserModule {

    @Singleton
    @Binds
    abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}