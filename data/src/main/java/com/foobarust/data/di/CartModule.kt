package com.foobarust.data.di

import com.foobarust.data.repositories.CartRepositoryImpl
import com.foobarust.domain.repositories.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module(includes = [
    PersistentModule::class
])
@InstallIn(ApplicationComponent::class)
abstract class CartModule {

    @Singleton
    @Binds
    abstract fun bindsCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository
}