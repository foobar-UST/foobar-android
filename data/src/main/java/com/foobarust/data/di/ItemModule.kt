package com.foobarust.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

/**
 * Created by kevin on 10/13/20
 */

@Module(includes = [PersistentModule::class])
@InstallIn(ApplicationComponent::class)
abstract class ItemModule {


}