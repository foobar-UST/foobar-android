package com.foobarust.android.di

import com.foobarust.domain.repositories.*
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.*
import kotlinx.coroutines.CoroutineScope

/**
 * Created by kevin on 4/30/21
 */

class RepositoryContainer(coroutineScope: CoroutineScope) {

    private val dependencyContainer = DependencyContainer()

    val authRepository: AuthRepository = FakeAuthRepositoryImpl(
        idToken = dependencyContainer.fakeIdToken,
        defaultAuthProfile = dependencyContainer.fakeAuthProfile,
        isSignedIn = true,
        coroutineScope = coroutineScope
    )

    val cartRepository: CartRepository = FakeCartRepositoryImpl(
        idToken = dependencyContainer.fakeIdToken,
        defaultUserCart = dependencyContainer.fakeUserCart,
        defaultCartItems = dependencyContainer.fakeUserCartItems
    )

    val checkoutRepository: CheckoutRepository = FakeCheckoutRepositoryImpl(
        idToken = dependencyContainer.fakeIdToken
    )

    val mapRepository: MapRepository = FakeMapRepositoryImpl()

    val messagingRepository: MessagingRepository = FakeMessagingRepositoryImpl(
        idToken = dependencyContainer.fakeIdToken
    )

    val orderRepository: OrderRepository = FakeOrderRepositoryImpl()

    val promotionRepository: PromotionRepository = FakePromotionRepositoryImpl()

    val sellerRepository: SellerRepository = FakeSellerRepositoryImpl()

    val userRepository: UserRepository = FakeUserRepositoryImpl(
        idToken = dependencyContainer.fakeIdToken,
        defaultUserDetail = dependencyContainer.fakeUserDetail,
        hasCompletedTutorial = true
    )
}