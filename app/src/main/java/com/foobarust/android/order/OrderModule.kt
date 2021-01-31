package com.foobarust.android.order

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Created by kevin on 1/30/21
 */

@Module
@InstallIn(ViewModelComponent::class)
object OrderModule {

    @Provides
    @ViewModelScoped
    fun provideOrderStateDescriptionUtil(@ApplicationContext context: Context): OrderStateDescriptionUtil {
        return OrderStateDescriptionUtil(context)
    }
}