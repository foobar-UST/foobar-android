package com.foobarust.data.di

import com.foobarust.data.repositories.MapRepositoryImpl
import com.foobarust.domain.repositories.MapRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kevin on 1/4/21
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class MapModule {

    @Singleton
    @Binds
    abstract fun bindsMapRepository(
        mapRepositoryImpl: MapRepositoryImpl
    ) : MapRepository
}