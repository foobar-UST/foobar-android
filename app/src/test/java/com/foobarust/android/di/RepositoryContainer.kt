package com.foobarust.android.di

import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.*
import kotlinx.coroutines.CoroutineScope

/**
 * Created by kevin on 4/30/21
 */

class RepositoryContainer(coroutineScope: CoroutineScope) {

    private val dependencyContainer = DependencyContainer()

    val authRepository: FakeAuthRepositoryImpl = FakeAuthRepositoryImpl(
        idToken = dependencyContainer.fakeIdToken,
        defaultAuthProfile = dependencyContainer.fakeAuthProfile,
        isSignedIn = true,
        coroutineScope = coroutineScope
    )

    val cartRepository: FakeCartRepositoryImpl = FakeCartRepositoryImpl(
        idToken = dependencyContainer.fakeIdToken,
        defaultUserCart = dependencyContainer.fakeUserCart,
        defaultCartItems = dependencyContainer.fakeUserCartItems
    )

    val checkoutRepository: FakeCheckoutRepositoryImpl = FakeCheckoutRepositoryImpl(
        idToken = dependencyContainer.fakeIdToken
    )

    val mapRepository: FakeMapRepositoryImpl = FakeMapRepositoryImpl()

    val messagingRepository: FakeMessagingRepositoryImpl = FakeMessagingRepositoryImpl(
        idToken = dependencyContainer.fakeIdToken
    )

    val orderRepository: FakeOrderRepositoryImpl = FakeOrderRepositoryImpl()

    val promotionRepository: FakePromotionRepositoryImpl = FakePromotionRepositoryImpl()

    val sellerRepository: FakeSellerRepositoryImpl = FakeSellerRepositoryImpl()

    val userRepository: FakeUserRepositoryImpl = FakeUserRepositoryImpl(
        idToken = dependencyContainer.fakeIdToken,
        defaultUserDetail = dependencyContainer.fakeUserDetail,
        hasCompletedTutorial = true
    )

    fun setUserSignedIn(value: Boolean) {
        authRepository.setUserSignedIn(value)
    }

    fun setNetworkError(value: Boolean) {
        authRepository.setNetworkError(value)
        cartRepository.setNetworkError(value)
        checkoutRepository.setNetworkError(value)
        mapRepository.setNetworkError(value)
        orderRepository.setNetworkError(value)
        promotionRepository.setNetworkError(value)
        sellerRepository.setNetworkError(value)
        userRepository.setNetworkError(value)
    }
}