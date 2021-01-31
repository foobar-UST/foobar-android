package com.foobarust.data.di

import com.foobarust.data.mappers.CheckoutMapper
import com.foobarust.data.repositories.CheckoutRepositoryImpl
import com.foobarust.domain.repositories.CheckoutRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kevin on 1/9/21
 */

@Module(includes = [PersistentModule::class])
@InstallIn(SingletonComponent::class)
abstract class CheckoutModule {

    @Singleton
    @Binds
    abstract fun bindsCheckoutRepository(
        checkoutRepositoryImpl: CheckoutRepositoryImpl
    ): CheckoutRepository

    companion object {
        @Singleton
        @Provides
        fun provideCheckoutMapper(): CheckoutMapper {
            return CheckoutMapper()
        }
    }
}