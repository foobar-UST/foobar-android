package com.foobarust.android.auth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

/**
 * Created by kevin on 9/23/20
 */

@Module
@InstallIn(ActivityRetainedComponent::class)
object AuthModule {

    @Provides
    @ActivityRetainedScoped
    fun provideAuthEmailUtil(): AuthEmailUtil {
        return AuthEmailUtil()
    }
}