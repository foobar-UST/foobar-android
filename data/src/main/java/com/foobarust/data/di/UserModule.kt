package com.foobarust.data.di

import com.foobarust.data.db.AppDatabase
import com.foobarust.data.db.UserDao
import com.foobarust.data.repositories.UserRepositoryImpl
import com.foobarust.domain.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kevin on 9/27/20
 */

@Module(includes = [PersistentModule::class])
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    companion object {
        @Singleton
        @Provides
        fun provideUserDao(appDatabase: AppDatabase): UserDao {
            return appDatabase.userDao()
        }
    }

    @Singleton
    @Binds
    abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}