package com.foobarust.data.di

import android.content.Context
import android.content.SharedPreferences
import com.foobarust.data.BuildConfig.*
import com.foobarust.data.common.Constants.CF_REQUEST_URL
import com.foobarust.data.common.Constants.GM_DIR_URL
import com.foobarust.data.common.Constants.PREFS_NAME
import com.foobarust.data.remoteapi.MapService
import com.foobarust.data.remoteapi.RemoteService
import com.foobarust.data.retrofit.ResourceCallAdapterFactory
import com.foobarust.data.retrofit.ResourceConverterFactory
import com.foobarust.data.retrofit.SupportHeadersInterceptor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by kevin on 9/28/20
 */

@Module
@InstallIn(ApplicationComponent::class)
object PersistentModule {

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        val settings = firestoreSettings {
            isPersistenceEnabled = false
            if (USE_FIREBASE_EMULATOR) {
                host = "$FIREBASE_EMULATOR_HOST:$FIREBASE_EMULATOR_FIRESTORE_PORT"
                isSslEnabled = false
            }
        }

        return Firebase.firestore.apply {
            firestoreSettings = settings
        }
    }

    @Singleton
    @Provides
    fun provideStorageReference(): StorageReference {
        return Firebase.storage.reference
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val builder = OkHttpClient.Builder().apply {
            addInterceptor(SupportHeadersInterceptor())
            addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRemoteService(okHttpClient: OkHttpClient): RemoteService {
        val url = if (USE_FIREBASE_EMULATOR) {
            "http://$FIREBASE_EMULATOR_HOST:$FIREBASE_EMULATOR_FUNCTIONS_PORT/foobar-group-delivery-app/us-central1/api/"
        } else {
            CF_REQUEST_URL
        }

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(url)
            .addConverterFactory(ResourceConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResourceCallAdapterFactory())
            .build()
            .create(RemoteService::class.java)
    }

    @Singleton
    @Provides
    fun provideMapService(okHttpClient: OkHttpClient): MapService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(GM_DIR_URL)
            // add converter
            .build()
            .create(MapService::class.java)
    }
}