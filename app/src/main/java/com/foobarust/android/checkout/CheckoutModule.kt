package com.foobarust.android.checkout

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

/**
 * Created by kevin on 1/25/21
 */

@Module
@InstallIn(ActivityRetainedComponent::class)
object CheckoutModule {

    @Provides
    @ActivityRetainedScoped
    fun providePaymentMethodUtil(@ApplicationContext context: Context): PaymentMethodUtil {
        return PaymentMethodUtil(context)
    }
}