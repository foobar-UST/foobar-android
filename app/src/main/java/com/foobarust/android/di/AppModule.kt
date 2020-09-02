package com.foobarust.android.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat
import com.foobarust.domain.di.DispatcherModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Created by kevin on 8/27/20
 */

@Module(includes = [DispatcherModule::class])
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
    }
}