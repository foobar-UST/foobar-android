package com.foobarust.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by kevin on 10/13/20
 */

@Module(includes = [PersistentModule::class])
@InstallIn(SingletonComponent::class)
abstract class ItemModule {


}