package com.foobarust.domain.usecases.cart

import com.foobarust.domain.di.DependencyContainer
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.repository.FakeSellerRepositoryImpl
import com.foobarust.domain.serialize.toSellerDetail
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by kevin on 4/28/21
 */

class CheckCartModifiableUseCaseTest {

    private lateinit var checkCartModifiableUseCase: CheckCartModifiableUseCase
    private lateinit var fakeSellerRepositoryImpl: FakeSellerRepositoryImpl
    private lateinit var dependencyContainer: DependencyContainer

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()
        fakeSellerRepositoryImpl = FakeSellerRepositoryImpl()
        checkCartModifiableUseCase = CheckCartModifiableUseCase()
    }

    @Test
    fun `test allow modify cart`() {
        val userCart = dependencyContainer.fakeUserCart
        val cartItems = dependencyContainer.fakeUserCartItems
        val sellerDetail = fakeSellerRepositoryImpl.sellerList
            .first { it.id == userCart.sellerId }
            .toSellerDetail()

        val result = checkCartModifiableUseCase(userCart, cartItems, sellerDetail)
        assertTrue(result)
    }

    @Test
    fun `test cart is not sync, reject modification`() {
        val userCart = dependencyContainer.fakeUserCart.copy(syncRequired = true)
        val cartItems = dependencyContainer.fakeUserCartItems
        val sellerDetail = fakeSellerRepositoryImpl.sellerList
            .first { it.id == userCart.sellerId }
            .toSellerDetail()

        val result = checkCartModifiableUseCase(userCart, cartItems, sellerDetail)
        assertFalse(result)
    }

    @Test
    fun `test cart item unavailable, reject modification`() {
        val userCart = dependencyContainer.fakeUserCart
        val cartItems = dependencyContainer.fakeUserCartItems.mapIndexed { index, userCartItem ->
            if (index == 0) userCartItem.copy(available = false) else userCartItem
        }
        val sellerDetail = fakeSellerRepositoryImpl.sellerList
            .first { it.id == userCart.sellerId }
            .toSellerDetail()

        val result = checkCartModifiableUseCase(userCart, cartItems, sellerDetail)
        assertFalse(result)
    }

    @Test
    fun `test cart item empty, reject modification`() {
        val userCart = dependencyContainer.fakeUserCart.copy(itemsCount = 0)
        val cartItems = emptyList<UserCartItem>()
        val sellerDetail = fakeSellerRepositoryImpl.sellerList
            .first { it.id == userCart.sellerId }
            .toSellerDetail()

        val result = checkCartModifiableUseCase(userCart, cartItems, sellerDetail)
        assertFalse(result)
    }

    @Test
    fun `test seller offline, reject modification`() {
        val userCart = dependencyContainer.fakeUserCart
        val cartItems = dependencyContainer.fakeUserCartItems
        val sellerDetail = fakeSellerRepositoryImpl.sellerList
            .first { it.id == userCart.sellerId }
            .toSellerDetail()
            .copy(online = false)

        val result = checkCartModifiableUseCase(userCart, cartItems, sellerDetail)
        assertFalse(result)
    }
}