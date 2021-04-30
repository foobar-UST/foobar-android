package com.foobarust.testshared.repositories

import com.foobarust.domain.models.cart.AddUserCartItem
import com.foobarust.domain.models.cart.UpdateUserCartItem
import com.foobarust.domain.models.cart.UserCart
import com.foobarust.domain.models.cart.UserCartItem
import com.foobarust.domain.repositories.CartRepository
import com.foobarust.domain.states.Resource
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * Created by kevin on 4/19/21
 */

class FakeCartRepositoryImpl(
    private val idToken: String,
    private val defaultUserCart: UserCart,
    defaultCartItems: List<UserCartItem>,
    private var newItemsSellerId: String = defaultUserCart.sellerId,
    private var newItemsSectionId: String? = defaultUserCart.sectionId
) : CartRepository {

    private var shouldReturnNetworkError = false
    private var shouldReturnDiffItemSeller = false

    private val _userCart = MutableStateFlow<UserCart?>(defaultUserCart)
    private val _cartItems = MutableStateFlow(defaultCartItems)

    override fun getUserCartObservable(userId: String): Flow<Resource<UserCart>> = flow {
        emit(Resource.Loading())
        if (shouldReturnNetworkError || _userCart.value == null) {
            emit(Resource.Error("Network error."))
        } else {
            emitAll(_userCart.map { Resource.Success(it!!) })
        }
    }

    override suspend fun clearUserCart(idToken: String) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        _userCart.value = null
        _cartItems.value = emptyList()
    }

    override suspend fun syncUserCart(idToken: String) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
    }

    override fun getUserCartItemsObservable(userId: String): Flow<Resource<List<UserCartItem>>> = flow {
        emit(Resource.Loading())
        if (shouldReturnNetworkError) {
            emit(Resource.Error("Network error."))
        } else {
            emitAll(_cartItems.map { Resource.Success(it) })
        }
    }

    override suspend fun addUserCartItem(idToken: String, addUserCartItem: AddUserCartItem) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        if (this.idToken != idToken) throw Exception("Invalid id token.")
        if (shouldReturnDiffItemSeller) throw Exception("Attempt to add item to cart from a different seller.")

        val newCartItems = _cartItems.value.toMutableList()

        newCartItems.add(UserCartItem(
            id = UUID.randomUUID().toString(),
            itemId = addUserCartItem.itemId,
            itemSellerId = newItemsSellerId,
            itemSectionId = newItemsSectionId,
            itemTitle = "item title",
            itemTitleZh = "item title zh",
            itemPrice = 20.0,
            itemImageUrl = "about:blank",
            amounts = 1,
            totalPrice = 20.0,
            available = true,
            updatedAt = Date()
        ))

        _cartItems.value = newCartItems

        val newSubtotalCost = newCartItems.sumOf { it.totalPrice }
        val prevUserCart = _userCart.value

        _userCart.value = prevUserCart?.copy(
            itemsCount = newCartItems.size,
            subtotalCost = newSubtotalCost,
            totalCost = newSubtotalCost + prevUserCart.deliveryCost,
            updatedAt = Date()
        ) ?: defaultUserCart.copy(
            itemsCount = newCartItems.size,
            subtotalCost = newSubtotalCost,
            totalCost = newSubtotalCost + defaultUserCart.deliveryCost,
            updatedAt = Date()
        )
    }

    override suspend fun updateUserCartItem(
        idToken: String,
        updateUserCartItem: UpdateUserCartItem
    ) {
        if (shouldReturnNetworkError) throw Exception("Network error.")
        if (this.idToken != idToken) throw Exception("Invalid id token.")
        if (shouldReturnDiffItemSeller) throw Exception("Attempt to add item to cart from a different seller.")

        val selectedItem = _cartItems.value.firstOrNull {
            it.id == updateUserCartItem.cartItemId
        } ?: throw Exception("Not cart item with respected id.")

        val newCartItems = _cartItems.value.toMutableList()

        newCartItems.add(UserCartItem(
            id = selectedItem.id,
            itemId = selectedItem.itemId,
            itemSellerId = selectedItem.itemSellerId,
            itemSectionId = selectedItem.itemSectionId,
            itemTitle = selectedItem.itemTitle,
            itemTitleZh = selectedItem.itemTitleZh,
            itemPrice = selectedItem.itemPrice,
            itemImageUrl = selectedItem.itemImageUrl,
            amounts = selectedItem.amounts,
            totalPrice = selectedItem.totalPrice,
            available = selectedItem.available,
            updatedAt = selectedItem.updatedAt
        ))

        _cartItems.value = newCartItems

        val newSubtotalCost = newCartItems.sumOf { it.totalPrice }
        val prevUserCart = _userCart.value

        _userCart.value = prevUserCart?.copy(
            itemsCount = newCartItems.size,
            subtotalCost = newSubtotalCost,
            totalCost = newSubtotalCost + prevUserCart.deliveryCost,
            updatedAt = Date()
        ) ?: defaultUserCart.copy(
            itemsCount = newCartItems.size,
            subtotalCost = newSubtotalCost,
            totalCost = newSubtotalCost + defaultUserCart.deliveryCost,
            updatedAt = Date()
        )
    }

    fun setNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    fun setNewItemsSellerId(sellerId: String) {
        newItemsSellerId = sellerId
    }

    fun setNewItemsSectionId(sectionId: String?) {
        newItemsSectionId = sectionId
    }
}