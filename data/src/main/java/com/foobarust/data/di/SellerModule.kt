package com.foobarust.data.di

import com.foobarust.data.mappers.SellerMapper
import com.foobarust.data.repositories.SellerRepositoryImpl
import com.foobarust.domain.repositories.SellerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kevin on 9/28/20
 */

@Module(includes = [PersistentModule::class])
@InstallIn(SingletonComponent::class)
abstract class SellerModule {

    @Singleton
    @Binds
    abstract fun bindsSellerRepository(
        sellerRepositoryImpl: SellerRepositoryImpl
    ): SellerRepository

    companion object {
        @Singleton
        @Provides
        fun provideSellerMapper(): SellerMapper {
            return SellerMapper()
        }
    }
}