package com.foobarust.android.di

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.foobarust.android.utils.DynamicLinksUtils
import com.foobarust.android.utils.ResourceIdentifier
import com.foobarust.domain.di.CoroutineModule
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [CoroutineModule::class])
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
    }

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideCrashlytics(): FirebaseCrashlytics {
        return Firebase.crashlytics
    }

    @Provides
    @Singleton
    fun provideDynamicLinks(): FirebaseDynamicLinks{
        return Firebase.dynamicLinks
    }

    @Provides
    @Singleton
    fun provideMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideResourceIdentifier(@ApplicationContext context: Context): ResourceIdentifier {
        return ResourceIdentifier(context)
    }

    @Provides
    @Singleton
    fun provideDynamicLinksUtils(firebaseDynamicLinks: FirebaseDynamicLinks): DynamicLinksUtils {
        return DynamicLinksUtils(firebaseDynamicLinks)
    }
}