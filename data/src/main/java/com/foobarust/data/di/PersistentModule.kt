package com.foobarust.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.foobarust.data.BuildConfig.*
import com.foobarust.data.api.MapService
import com.foobarust.data.api.RemoteService
import com.foobarust.data.common.Constants.APP_DB_NAME
import com.foobarust.data.common.Constants.MAPS_API_URL
import com.foobarust.data.common.Constants.PREFS_NAME
import com.foobarust.data.common.Constants.REMOTE_REQUEST_URL
import com.foobarust.data.db.AppDatabase
import com.foobarust.data.json.DirectionsDeserializer
import com.foobarust.data.models.maps.DirectionsResponse
import com.foobarust.data.retrofit.RemoteResponseInterceptor
import com.foobarust.data.retrofit.RequestHeadersInterceptor
import com.foobarust.data.retrofit.SuccessResponseConverterFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
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
    fun provideLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideDefaultHttpClientBuilder(loggingInterceptor: Interceptor): OkHttpClient.Builder {
        return OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
        }
    }

    @Singleton
    @Provides
    fun provideRemoteService(defaultHttpClientBuilder: OkHttpClient.Builder): RemoteService {
        val url = if (USE_FIREBASE_EMULATOR) {
            "http://$FIREBASE_EMULATOR_HOST:$FIREBASE_EMULATOR_FUNCTIONS_PORT/foobar-group-delivery-app/us-central1/api/"
        } else {
            REMOTE_REQUEST_URL
        }

        val clientBuilder = defaultHttpClientBuilder.apply {
            addInterceptor(RequestHeadersInterceptor())
            addInterceptor(RemoteResponseInterceptor())
        }

        return Retrofit.Builder()
            .client(clientBuilder.build())
            .baseUrl(url)
            .addConverterFactory(SuccessResponseConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteService::class.java)
    }

    @Singleton
    @Provides
    fun provideMapService(defaultHttpClientBuilder: OkHttpClient.Builder): MapService {
        val customGson = GsonBuilder().registerTypeAdapter(
            DirectionsResponse::class.java,
            DirectionsDeserializer()
        ).create()

        return Retrofit.Builder()
            .client(defaultHttpClientBuilder.build())
            .baseUrl(MAPS_API_URL)
            .addConverterFactory(GsonConverterFactory.create(customGson))
            .build()
            .create(MapService::class.java)
    }
}