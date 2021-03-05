package com.foobarust.data.di

import com.foobarust.data.db.AppDatabase
import com.foobarust.data.db.OrdersBasicDao
import com.foobarust.data.db.OrdersDetailDao
import com.foobarust.data.mappers.OrderMapper
import com.foobarust.data.repositories.OrderRepositoryImpl
import com.foobarust.domain.repositories.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kevin on 1/29/21
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderModule {

    @Singleton
    @Binds
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    companion object {
        @Singleton
        @Provides
        fun provideOrdersBasicDao(appDatabase: AppDatabase): OrdersBasicDao {
            return appDatabase.ordersBasicDao()
        }

        @Singleton
        @Provides
        fun provideOrdersDetailDao(appDatabase: AppDatabase): OrdersDetailDao {
            return appDatabase.ordersDetailDao()
        }

        @Singleton
        @Provides
        fun provideOrderMapper(): OrderMapper {
            return OrderMapper()
        }
    }
}