package com.foobarust.data.di

import com.foobarust.data.repositories.MessagingRepositoryImpl
import com.foobarust.domain.repositories.MessagingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kevin on 2/6/21
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class MessagingModule {

    @Singleton
    @Binds
    abstract fun bindMessagingRepository(
        messagingRepositoryImpl: MessagingRepositoryImpl
    ) : MessagingRepository
}