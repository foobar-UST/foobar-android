package com.foobarust.data.di

import com.foobarust.data.db.AppDatabase
import com.foobarust.data.db.UserDetailDao
import com.foobarust.data.mappers.UserMapper
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

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Singleton
    @Binds
    abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    companion object {
        @Singleton
        @Provides
        fun provideUserDetailDao(appDatabase: AppDatabase): UserDetailDao {
            return appDatabase.userDetailDao()
        }

        @Singleton
        @Provides
        fun provideUserMapper(): UserMapper {
            return UserMapper()
        }
    }
}