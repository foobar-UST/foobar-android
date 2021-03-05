package com.foobarust.data.di

import com.foobarust.data.mappers.PromotionMapper
import com.foobarust.data.repositories.PromotionRepositoryImpl
import com.foobarust.domain.repositories.PromotionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kevin on 10/3/20
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class PromotionModule {

    @Singleton
    @Binds
    abstract fun bindsPromotionRepository(
        promotionRepositoryImpl: PromotionRepositoryImpl
    ): PromotionRepository

    companion object {
        @Singleton
        @Provides
        fun providePromotionMapper(): PromotionMapper {
            return PromotionMapper()
        }
    }
}