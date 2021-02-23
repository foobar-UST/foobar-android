package com.foobarust.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.foobarust.data.BuildConfig.*
import com.foobarust.data.api.RemoteService
import com.foobarust.data.constants.Constants.APP_DB_NAME
import com.foobarust.data.constants.Constants.PREFS_NAME
import com.foobarust.data.constants.Constants.REMOTE_REQUEST_URL
import com.foobarust.data.db.AppDatabase
import com.foobarust.data.retrofit.RemoteResponseInterceptor
import com.foobarust.data.retrofit.RequestHeadersInterceptor
import com.foobarust.data.retrofit.SuccessResponseConverterFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by kevin on 9/28/20
 */

@Module
@InstallIn(SingletonComponent::class)
object PersistentModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            APP_DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        val settings = if (USE_FIREBASE_EMULATOR) {
            firestoreSettings {
                isPersistenceEnabled = false
                host = "$EMULATOR_HOST:$EMULATOR_FIRESTORE_PORT"
                isSslEnabled = false
            }
        } else {
            firestoreSettings {
                isPersistenceEnabled = false
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
        return OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            addInterceptor(RequestHeadersInterceptor())
            addInterceptor(RemoteResponseInterceptor())
        }.build()
    }

    @Singleton
    @Provides
    fun provideRemoteService(okHttpClient: OkHttpClient): RemoteService {
        val url = if (USE_FIREBASE_EMULATOR) {
            "http://$EMULATOR_HOST:$EMULATOR_FUNCTIONS_PORT/" +
                "foobar-group-delivery-app/us-central1/api/"
        } else {
            REMOTE_REQUEST_URL
        }

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(url)
            .addConverterFactory(SuccessResponseConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteService::class.java)
    }
}