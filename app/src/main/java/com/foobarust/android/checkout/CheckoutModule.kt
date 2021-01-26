package com.foobarust.android.checkout

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Created by kevin on 1/25/21
 */

@Module
@InstallIn(ViewModelComponent::class)
object CheckoutModule {

    @Provides
    @ViewModelScoped
    fun providePaymentMethodUtil(@ApplicationContext context: Context): PaymentMethodUtil {
        return PaymentMethodUtil(context)
    }
}