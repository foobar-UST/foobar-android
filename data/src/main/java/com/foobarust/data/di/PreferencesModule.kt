package com.foobarust.data.di

import com.foobarust.data.repositories.SharedPreferencesRepository
import com.foobarust.domain.repositories.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * Created by kevin on 8/28/20
 */

@Module
@InstallIn(ApplicationComponent::class)
abstract class PreferencesModule {

    @Singleton
    @Binds
    abstract fun providePreferencesRepository(
        sharedPreferencesRepository: SharedPreferencesRepository
    ): PreferencesRepository
}
