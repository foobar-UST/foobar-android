package com.foobarust.data.di

import com.foobarust.data.api.MapService
import com.foobarust.data.constants.Constants.MAPS_API_URL
import com.foobarust.data.json.DirectionsDeserializer
import com.foobarust.data.models.maps.DirectionsResponse
import com.foobarust.data.repositories.MapRepositoryImpl
import com.foobarust.domain.repositories.MapRepository
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by kevin on 1/4/21
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class MapModule {

    @Singleton
    @Binds
    abstract fun bindsMapRepository(
        mapRepositoryImpl: MapRepositoryImpl
    ) : MapRepository

    companion object {
        @Singleton
        @Provides
        fun provideMapService(): MapService {
            val gson = GsonBuilder().registerTypeAdapter(
                DirectionsResponse::class.java,
                DirectionsDeserializer()
            ).create()

            return Retrofit.Builder()
                .baseUrl(MAPS_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(MapService::class.java)
        }
    }
}