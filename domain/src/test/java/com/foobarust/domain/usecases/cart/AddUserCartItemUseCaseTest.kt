package com.foobarust.domain.usecases.cart

import com.foobarust.domain.models.cart.AddUserCartItem
import com.foobarust.domain.states.Resource
import com.foobarust.testshared.di.DependencyContainer
import com.foobarust.testshared.repositories.FakeAuthRepositoryImpl
import com.foobarust.testshared.repositories.FakeCartRepositoryImpl
import com.foobarust.testshared.utils.TestCoroutineRule
import com.foobarust.testshared.utils.coroutineScope
import com.foobarust.testshared.utils.runBlockingTest
import kotlinx.coroutines.flow.toList
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Created by kevin on 4/28/21
 */

class AddUserCartItemUseCaseTest {

    private lateinit var addUserCartItemUseCase: AddUserCartItemUseCase
    private lateinit var fakeAuthRepositoryImpl: FakeAuthRepositoryImpl
    private lateinit var fakeCartRepositoryImpl: FakeCartRepositoryImpl
    private lateinit var dependencyContainer: DependencyContainer

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Before
    fun init() {
        dependencyContainer = DependencyContainer()

        fakeAuthRepositoryImpl = FakeAuthRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultAuthProfile = dependencyContainer.fakeAuthProfile,
            isSignedIn = true,
            coroutineScope = coroutineRule.coroutineScope()
        )

        fakeCartRepositoryImpl = FakeCartRepositoryImpl(
            idToken = dependencyContainer.fakeIdToken,
            defaultUserCart = dependencyContainer.fakeUserCart,
            defaultCartItems = dependencyContainer.fakeUserCartItems
        )

        addUserCartItemUseCase = AddUserCartItemUseCase(
            authRepository = fakeAuthRepositoryImpl,
            cartRepository = fakeCartRepositoryImpl,
            coroutineDispatcher = coroutineRule.testDispatcher
        )
    }

    @Test
    fun `test add cart item success`() = coroutineRule.runBlockingTest {
        fakeCartRepositoryImpl.setNetworkError(false)
        fakeAuthRepositoryImpl.setUserSignedIn(true)

        val addUserCartItem = AddUserCartItem(
            itemId = UUID.randomUUID().toString(),
            amounts = 1,
            sectionId = null
        )

        val params = AddUserCartItemParameters(
            addUserCartItems = listOf(addUserCartItem),
            itemSellerId = dependencyContainer.fakeUserCart.sellerId,
            cartSellerId = dependencyContainer.fakeUserCart.sellerId
        )

        val result = addUserCartItemUseCase(params).toList()
        assert(result.last() is Resource.Success)
    }

    @Test
    fun `test add cart item network error`() = coroutineRule.runBlockingTest {
        fakeCartRepositoryImpl.setNetworkError(true)
        fakeAuthRepositoryImpl.setUserSignedIn(true)

        val addUserCartItem = AddUserCartItem(
            itemId = UUID.randomUUID().toString(),
            amounts = 1,
            sectionId = null
        )

        val params = AddUserCartItemParameters(
            addUserCartItems = listOf(addUserCartItem),
            itemSellerId = dependencyContainer.fakeUserCart.sellerId,
            cartSellerId = dependencyContainer.fakeUserCart.sellerId
        )

        val result = addUserCartItemUseCase(params).toList()
        assert(result.last() is Resource.Error)
    }

    @Test
    fun `test add cart item not signed in error`() = coroutineRule.runBlockingTest {
        fakeCartRepositoryImpl.setNetworkError(false)
        fakeAuthRepositoryImpl.setUserSignedIn(false)

        val addUserCartItem = AddUserCartItem(
            itemId = UUID.randomUUID().toString(),
            amounts = 1,
            sectionId = null
        )

        val params = AddUserCartItemParameters(
            addUserCartItems = listOf(addUserCartItem),
            itemSellerId = dependencyContainer.fakeUserCart.sellerId,
            cartSellerId = dependencyContainer.fakeUserCart.sellerId
        )

        val result = addUserCartItemUseCase(params).toList()
        assert(result.last() is Resource.Error)
    }

    @Test
    fun `test add cart item multiple seller error`() = coroutineRule.runBlockingTest {
        fakeCartRepositoryImpl.setNetworkError(false)
        fakeAuthRepositoryImpl.setUserSignedIn(true)

        val addUserCartItem = AddUserCartItem(
            itemId = UUID.randomUUID().toString(),
            amounts = 1,
            sectionId = null
        )

        val params = AddUserCartItemParameters(
            addUserCartItems = listOf(addUserCartItem),
            itemSellerId = dependencyContainer.fakeUserCart.sellerId,
            cartSellerId = "5f71cda210cb0cc92321987e"
        )

        val result = addUserCartItemUseCase(params).toList()
        assert(result.last() is Resource.Error)
    }
}