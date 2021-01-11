package com.foobarust.data.di

import com.foobarust.data.repositories.CheckoutRepositoryImpl
import com.foobarust.domain.repositories.CheckoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * Created by kevin on 1/9/21
 */

@Module(includes = [PersistentModule::class])
@InstallIn(ApplicationComponent::class)
abstract class CheckoutModule {

    @Singleton
    @Binds
    abstract fun bindsCheckoutRepository(
        checkoutRepositoryImpl: CheckoutRepositoryImpl
    ): CheckoutRepository
}