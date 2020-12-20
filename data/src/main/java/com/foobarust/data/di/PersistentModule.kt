package com.foobarust.data.di

import android.content.Context
import android.content.SharedPreferences
import com.foobarust.data.BuildConfig
import com.foobarust.data.common.Constants.PREFS_NAME
import com.foobarust.data.common.Constants.REMOTE_URL
import com.foobarust.data.remoteapi.RemoteService
import com.foobarust.data.utils.ResourceCallAdapterFactory
import com.foobarust.data.utils.SupportHeadersInterceptor
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
            if (BuildConfig.USE_FIREBASE_EMULATOR) {
                host = "${BuildConfig.FIREBASE_EMULATOR_HOST}:${BuildConfig.FIREBASE_EMULATOR_FIRESTORE_PORT}"
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
    fun provideRemoteService(client: OkHttpClient): RemoteService {
        val remoteUrl = if (BuildConfig.USE_FIREBASE_EMULATOR) {
            "http://${BuildConfig.FIREBASE_EMULATOR_HOST}:${BuildConfig.FIREBASE_EMULATOR_FUNCTIONS_PORT}/foobar-group-delivery-app/us-central1/api/"
        } else {
            REMOTE_URL
        }

        return Retrofit.Builder()
            .baseUrl(remoteUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResourceCallAdapterFactory())
            .build()
            .create(RemoteService::class.java)
    }
}